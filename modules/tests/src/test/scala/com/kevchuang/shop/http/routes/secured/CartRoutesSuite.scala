package com.kevchuang.shop.http.routes.secured

import cats.data.Kleisli
import cats.effect.IO
import com.kevchuang.shop.domain.auth.UserId
import com.kevchuang.shop.domain.cart.{Cart, CartTotal}
import com.kevchuang.shop.domain.item.{ItemId, Quantity}
import com.kevchuang.shop.http.auth.users.CommonUser
import com.kevchuang.shop.services.ShoppingCart
import com.kevchuang.shop.suite.HttpSuite
import com.kevchuang.shop.utils.generators.*
import io.circe.generic.auto.*
import io.github.iltotore.iron.cats.given
import io.github.iltotore.iron.circe.given
import org.http4s.Method.*
import org.http4s.Status as HttpStatus
import org.http4s.circe.CirceEntityEncoder.*
import org.http4s.client.dsl.io.*
import org.http4s.server.AuthMiddleware
import org.http4s.syntax.literals.*
import squants.market.USD

object CartRoutesSuite extends HttpSuite:
  def authMiddleware(authUser: CommonUser): AuthMiddleware[IO, CommonUser] =
    AuthMiddleware(Kleisli.pure(authUser))

  def dataCart(cartTotal: CartTotal) =
    new TestCart:
      override def get(userId: UserId): IO[CartTotal] =
        IO.pure(cartTotal)

  test("GET shopping cart succeeds") {
    val gen = for
      u <- commonUserGen
      c <- cartTotalGen
    yield u -> c

    forall(gen) { case (user, ct) =>
      val req    = GET(uri"/cart")
      val routes = CartRoutes[IO](dataCart(ct)).routes(authMiddleware(user))
      expectHttpBodyAndStatus(routes, req)(ct, HttpStatus.Ok)
    }
  }

  test("POST add item to shopping cart succeeds") {
    val gen = for
      u <- commonUserGen
      c <- cartGen
    yield u -> c

    forall(gen) { case (user, c) =>
      val req = POST(c, uri"/cart")
      val routes =
        CartRoutes[IO](new TestCart).routes(authMiddleware(user))
      expectHttpStatus(routes, req)(HttpStatus.Created)
    }
  }

end CartRoutesSuite

protected class TestCart extends ShoppingCart[IO]:
  def add(userId: UserId, itemId: ItemId, quantity: Quantity): IO[Unit] =
    IO.unit
  def get(userId: UserId): IO[CartTotal] =
    IO.pure(CartTotal(List.empty, USD(0)))
  def delete(userId: UserId): IO[Unit]                     = IO.unit
  def removeItem(userId: UserId, itemId: ItemId): IO[Unit] = IO.unit
  def update(userId: UserId, cart: Cart): IO[Unit]         = IO.unit
end TestCart
