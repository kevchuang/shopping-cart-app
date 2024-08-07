package com.kevchuang.shop.http.routes.auth

import cats.Monad
import cats.syntax.all.*
import com.kevchuang.shop.http.auth.users.CommonUser
import com.kevchuang.shop.services.Auth
import dev.profunktor.auth.AuthHeaders
import org.http4s.*
import org.http4s.dsl.Http4sDsl
import org.http4s.server.{AuthMiddleware, Router}

final case class LogoutRoutes[F[_]: Monad](auth: Auth[F]) extends Http4sDsl[F]:
  private[routes] val prefixPath = "/auth"

  private val httpRoutes: AuthedRoutes[CommonUser, F] =
    AuthedRoutes.of[CommonUser, F]:
      case ar @ POST -> Root as user =>
        AuthHeaders
          .getBearerToken[F](ar.req)
          .traverse_(auth.logout(_, user.value.name))
          .flatMap(_ => NoContent())

  def routes(authMiddleware: AuthMiddleware[F, CommonUser]): HttpRoutes[F] =
    Router(
      prefixPath -> authMiddleware(httpRoutes)
    )

end LogoutRoutes
