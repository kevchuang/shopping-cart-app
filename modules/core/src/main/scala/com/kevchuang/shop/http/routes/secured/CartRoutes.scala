package com.kevchuang.shop.http.routes.secured

import cats.effect.Concurrent
import cats.syntax.all.*
import com.kevchuang.shop.domain.cart.Cart
import com.kevchuang.shop.domain.item.ItemId
import com.kevchuang.shop.http.auth.users.CommonUser
import com.kevchuang.shop.http.vars.ItemIdVar
import com.kevchuang.shop.services.ShoppingCart
import io.circe.generic.auto.*
import io.github.iltotore.iron.*
import io.github.iltotore.iron.circe.given
import org.http4s.circe.*
import org.http4s.circe.CirceEntityEncoder.*
import org.http4s.dsl.Http4sDsl
import org.http4s.server.{AuthMiddleware, Router}
import org.http4s.{AuthedRoutes, HttpRoutes}

final case class CartRoutes[F[_]: Concurrent](cart: ShoppingCart[F])
    extends Http4sDsl[F]:
  private[routes] val prefixPath = "/cart"

  private val httpRoutes: AuthedRoutes[CommonUser, F] =
    AuthedRoutes.of[CommonUser, F]:
      case GET -> Root as user =>
        Ok(cart.get(user.value.id))
      case ar @ POST -> Root as user =>
        ar.req
          .asJsonDecode[Cart]
          .flatMap(
            _.items
              .map(cart.add(user.value.id, _, _))
              .toList
              .sequence
              *> Created()
          )
      case ar @ PUT -> Root as user =>
        ar.req
          .asJsonDecode[Cart]
          .flatMap(c => cart.update(user.value.id, c) *> Ok())
      case DELETE -> Root / ItemIdVar(itemId) as user =>
        cart.removeItem(user.value.id, itemId) *> NoContent()

  def routes(authMiddleware: AuthMiddleware[F, CommonUser]): HttpRoutes[F] =
    Router(prefixPath -> authMiddleware(httpRoutes))

end CartRoutes
