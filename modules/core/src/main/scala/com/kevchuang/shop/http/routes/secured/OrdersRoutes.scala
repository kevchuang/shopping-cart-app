package com.kevchuang.shop.http.routes.secured

import cats.effect.Concurrent
import com.kevchuang.shop.domain.given
import com.kevchuang.shop.http.auth.users.CommonUser
import com.kevchuang.shop.http.vars.OrderIdVar
import com.kevchuang.shop.services.Orders
import io.circe.generic.auto.*
import io.github.iltotore.iron.*
import io.github.iltotore.iron.circe.given
import org.http4s.circe.CirceEntityEncoder.*
import org.http4s.dsl.Http4sDsl
import org.http4s.server.{AuthMiddleware, Router}
import org.http4s.{AuthedRoutes, HttpRoutes}

final case class OrdersRoutes[F[_]: Concurrent](orders: Orders[F])
    extends Http4sDsl[F]:
  private[routes] val prefixPath = "/orders"

  private val httpRoutes: AuthedRoutes[CommonUser, F] =
    AuthedRoutes.of[CommonUser, F]:
      case GET -> Root as user =>
        Ok(orders.findBy(user.value.id))

      case GET -> Root / OrderIdVar(orderId) as user =>
        Ok(orders.get(user.value.id, orderId))

  def routes(authMiddleware: AuthMiddleware[F, CommonUser]): HttpRoutes[F] =
    Router(prefixPath -> authMiddleware(httpRoutes))

end OrdersRoutes
