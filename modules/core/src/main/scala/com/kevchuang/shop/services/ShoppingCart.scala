package com.kevchuang.shop.services

import cats.effect.Sync
import cats.syntax.all.*
import com.kevchuang.shop.domain.auth.UserId
import com.kevchuang.shop.domain.cart.*
import com.kevchuang.shop.domain.given
import com.kevchuang.shop.domain.id.ID
import com.kevchuang.shop.domain.item.{ItemId, Quantity}
import com.kevchuang.shop.domain.types.common.RefineError
import dev.profunktor.redis4cats.RedisCommands
import io.github.iltotore.iron.*
import io.github.iltotore.iron.cats.*
import io.github.iltotore.iron.constraint.all.Positive

trait ShoppingCart[F[_]]:
  def add(userId: UserId, itemId: ItemId, quantity: Quantity): F[Unit]
  def get(userId: UserId): F[CartTotal]
  def delete(userId: UserId): F[Unit]
  def removeItem(userId: UserId, itemId: ItemId): F[Unit]
  def update(userId: UserId, cart: Cart): F[Unit]
end ShoppingCart

object ShoppingCart:
  def make[F[_]](
      items: Items[F],
      redis: RedisCommands[F, String, String],
      expiration: ShoppingCartExpiration
  )(using F: Sync[F]): ShoppingCart[F] =
    new ShoppingCart[F]:
      override def add(
          userId: UserId,
          itemId: ItemId,
          quantity: Quantity
      ): F[Unit] =
        for
          _ <- redis.hSet(
                 userId.value.toString,
                 itemId.value.toString,
                 quantity.value.toString
               )
          _ <- redis.expire(userId.value.toString, expiration.value)
        yield ()

      override def get(userId: UserId): F[CartTotal] =
        redis
          .hGetAll(userId.value.toString)
          .flatMap(
            _.toList
              .traverseFilter: (k, v) =>
                for
                  id <- ID.fromStr[F, ItemId](k)(ItemId(_))
                  quantity <- F.fromValidated(
                                v.toInt
                                  .refineValidated[Positive]
                                  .leftMap(RefineError(_))
                              ).map(Quantity(_))
                  item <- items
                            .findById(id)
                            .map(
                              _.map(_.cart(quantity))
                            )
                yield item
          )
          .map: items =>
            CartTotal(items, items.foldMap(_.subTotal))

      override def delete(userId: UserId): F[Unit] =
        redis
          .del(userId.value.toString)
          .void

      override def removeItem(userId: UserId, itemId: ItemId): F[Unit] =
        redis
          .hDel(userId.value.toString, itemId.value.toString)
          .void

      override def update(userId: UserId, cart: Cart): F[Unit] =
        redis
          .hGetAll(userId.value.toString)
          .flatMap(
            _.toList.traverse_((k, _) =>
              ID.fromStr[F, ItemId](k)(ItemId(_))
                .flatMap: id =>
                  cart.items
                    .get(id)
                    .traverse_(q =>
                      redis.hSet(userId.value.toString, k, q.value.toString)
                    )
            )
          )
end ShoppingCart
