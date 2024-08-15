package com.kevchuang.shop.http.routes.secured

import cats.effect.Concurrent
import cats.syntax.all.*
import com.kevchuang.shop.domain.cart.EmptyCartError
import com.kevchuang.shop.domain.checkout.Card
import com.kevchuang.shop.domain.order.OrderOrPaymentError
import com.kevchuang.shop.http.auth.users.CommonUser
import com.kevchuang.shop.programs.Checkout
import io.circe.generic.auto.*
import io.github.iltotore.iron.*
import io.github.iltotore.iron.circe.given
import org.http4s.circe.CirceEntityDecoder.*
import org.http4s.circe.CirceEntityEncoder.*
import org.http4s.dsl.Http4sDsl
import org.http4s.server.{AuthMiddleware, Router}
import org.http4s.{AuthedRoutes, HttpRoutes}

final case class CheckoutRoutes[F[_]: Concurrent](checkout: Checkout[F])
    extends Http4sDsl[F]:
  private[routes] val prefixPath = "/checkout"

  private val httpRoutes: AuthedRoutes[CommonUser, F] =
    AuthedRoutes.of[CommonUser, F]:
      case ar @ POST -> Root as user =>
        ar.req
          .decode[Card]: card =>
            checkout
              .process(user.value.id, card)
              .flatMap(Created(_))
          .recoverWith:
            case EmptyCartError         => BadRequest("Shopping cart is empty")
            case e: OrderOrPaymentError => BadRequest(e.show)

  def routes(authMiddleware: AuthMiddleware[F, CommonUser]): HttpRoutes[F] =
    Router(prefixPath -> authMiddleware(httpRoutes))
end CheckoutRoutes
