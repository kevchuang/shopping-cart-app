package com.kevchuang.shop.http.routes.auth

import cats.MonadThrow
import cats.effect.Concurrent
import cats.syntax.all.*
import com.kevchuang.shop.domain.auth.{*, given}
import com.kevchuang.shop.services.Auth
import io.circe.generic.auto.*
import io.github.iltotore.iron.*
import io.github.iltotore.iron.circe.given
import org.http4s.HttpRoutes
import org.http4s.circe.*
import org.http4s.circe.CirceEntityCodec.*
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router

final case class LoginRoutes[F[_]: Concurrent: MonadThrow: JsonDecoder](
    auth: Auth[F]
) extends Http4sDsl[F]:
  private[routes] val prefixPath = "/auth"

  private val httpRoutes: HttpRoutes[F] =
    HttpRoutes.of[F]:
      case request @ POST -> Root =>
        request
          .decode[LoginUser]: user =>
            auth
              .login(user.userName.toDomain, user.password.toDomain)
              .flatMap(Ok(_))
              .recoverWith:
                case InvalidPassword(_) | UserNotFound(_) => Forbidden()

  val routes: HttpRoutes[F] = Router(
    prefixPath -> httpRoutes
  )
end LoginRoutes
