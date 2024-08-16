package com.kevchuang.shop.services

import cats.data.NonEmptyList
import cats.effect.*
import cats.effect.std.UUIDGen
import cats.syntax.all.*
import com.kevchuang.shop.domain.auth.UserId
import com.kevchuang.shop.domain.cart.CartItem
import com.kevchuang.shop.domain.item.{ItemId, Quantity}
import com.kevchuang.shop.domain.order.{Order, OrderId}
import com.kevchuang.shop.domain.payment.PaymentId
import com.kevchuang.shop.sql.codecs.*
import io.github.iltotore.iron.*
import io.github.iltotore.iron.circe.given
import skunk.*
import skunk.circe.codec.all.*
import skunk.implicits.*
import squants.market.Money

trait Orders[F[_]]:
  def create(
      userId: UserId,
      paymentId: PaymentId,
      items: NonEmptyList[CartItem],
      total: Money
  ): F[OrderId]
  def findBy(userId: UserId): F[List[Order]]
  def get(userId: UserId, orderId: OrderId): F[Option[Order]]
end Orders

object Orders:
  def make[F[_]: UUIDGen: Concurrent](
      postgres: Resource[F, Session[F]]
  ): Orders[F] =
    import OrdersSQL.*
    new Orders[F]:
      def create(
          userId: UserId,
          paymentId: PaymentId,
          items: NonEmptyList[CartItem],
          total: Money
      ): F[OrderId] =
        postgres.use: session =>
          session
            .prepareR(insertOrder)
            .use: cmd =>
              for
                id <- UUIDGen.randomUUID[F].map(OrderId(_))
                itemsMap =
                  items.map(i => i.item.uuid -> i.quantity).toList.toMap
                _ <- cmd.execute(
                       userId -> Order(id, paymentId, itemsMap, total)
                     )
              yield id

      def findBy(userId: UserId): F[List[Order]] =
        postgres.use: session =>
          session
            .prepareR(selectByUserId)
            .use(
              _.stream(userId, 1024).compile.toList
            )

      def get(userId: UserId, orderId: OrderId): F[Option[Order]] =
        postgres.use: session =>
          session
            .prepareR(selectByUserIdAndOrderId)
            .use(_.option(userId -> orderId))
end Orders

object OrdersSQL:
  val decoder: Decoder[Order] =
    (orderId ~ userId ~ paymentId ~ jsonb[Map[ItemId, Quantity]] ~ price).map {
      case oid ~ _ ~ pid ~ items ~ total => Order(oid, pid, items, total)
    }

  val encoder: Encoder[UserId ~ Order] =
    (orderId ~ userId ~ paymentId ~ jsonb[Map[ItemId, Quantity]] ~ price)
      .contramap { case uid ~ o =>
        o.id -> uid -> o.paymentId -> o.items -> o.total
      }

  val insertOrder: Command[UserId ~ Order] =
    sql"""
      INSERT INTO orders
      VALUES ($encoder)
    """.command

  val selectByUserId: Query[UserId, Order] =
    sql"""
      SELECT * FROM orders
      WHERE user_id = $userId
    """.query(decoder)

  val selectByUserIdAndOrderId: Query[UserId ~ OrderId, Order] =
    sql"""
      SELECT * FROM orders
      WHERE user_id = $userId AND uuid = $orderId
    """.query(decoder)

end OrdersSQL
