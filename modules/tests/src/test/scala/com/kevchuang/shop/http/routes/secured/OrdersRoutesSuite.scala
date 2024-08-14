package com.kevchuang.shop.http.routes.secured

import cats.data.NonEmptyList
import cats.effect.IO
import com.kevchuang.shop.domain.auth.UserId
import com.kevchuang.shop.domain.cart.*
import com.kevchuang.shop.domain.given
import com.kevchuang.shop.domain.id.ID
import com.kevchuang.shop.domain.order.*
import com.kevchuang.shop.domain.payment.PaymentId
import com.kevchuang.shop.services.Orders
import com.kevchuang.shop.suite.HttpSuite
import com.kevchuang.shop.utils.generators.*
import io.circe.generic.auto.*
import io.github.iltotore.iron.*
import io.github.iltotore.iron.cats.given
import io.github.iltotore.iron.circe.given
import org.http4s.Method.*
import org.http4s.client.dsl.io.*
import org.http4s.syntax.literals.*
import org.http4s.{Uri, Status as HttpStatus, *}
import org.scalacheck.Gen
import squants.market.Money

object OrdersRoutesSuite extends HttpSuite:

  def dataOrders(orders: List[Order]): TestOrder =
    new TestOrder:
      override def findBy(userId: UserId): IO[List[Order]] = IO.pure(orders)

  def getOrder(order: Option[Order]): TestOrder =
    new TestOrder:
      override def get(userId: UserId, orderId: OrderId): IO[Option[Order]] =
        IO.pure(order)

  test("GET orders with user id"):
    val gen =
      for
        u <- commonUserGen
        o <- Gen.nonEmptyListOf(orderGen)
      yield (u, o)

    forall(gen) { (user, orders) =>
      val req = GET(uri"/orders")
      val routes =
        OrdersRoutes[IO](dataOrders(orders)).routes(authMiddleware(user))
      expectHttpBodyAndStatus(routes, req)(orders, HttpStatus.Ok)
    }

  test("GET orders with user id and order id"):
    val gen =
      for
        u <- commonUserGen
        o <- orderGen
      yield (u, o)

    forall(gen) { case (user, order) =>
      val req = GET(uri"/orders" / order.id.value.toString)
      val routes =
        OrdersRoutes[IO](getOrder(Option(order))).routes(authMiddleware(user))
      expectHttpBodyAndStatus(routes, req)(Option(order), HttpStatus.Ok)
    }

end OrdersRoutesSuite

protected class TestOrder extends Orders[IO]:
  def create(
      userId: UserId,
      paymentId: PaymentId,
      items: NonEmptyList[CartItem],
      total: Money
  ): IO[OrderId] =
    ID.make(OrderId(_))
  def findBy(userId: UserId): IO[List[Order]]                  = IO.pure(List.empty)
  def get(userId: UserId, orderId: OrderId): IO[Option[Order]] = IO.pure(None)
end TestOrder
