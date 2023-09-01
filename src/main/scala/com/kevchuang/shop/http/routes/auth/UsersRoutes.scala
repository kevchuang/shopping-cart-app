package com.kevchuang.shop.http.routes.auth

import cats.MonadThrow
import cats.effect.*
import cats.syntax.all.*
import com.kevchuang.shop.domain.auth.*
import com.kevchuang.shop.http.entities.*
import com.kevchuang.shop.services.Auth
import io.github.iltotore.iron.*
import io.github.iltotore.iron.cats.given
import org.http4s.HttpRoutes
import org.http4s.circe.*
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router

final case class UsersRoutes[F[_]: Concurrent: JsonDecoder: MonadThrow](
    auth: Auth[F]
) extends Http4sDsl[F]
    with HttpEntities[F]:
  private[routes] val prefixPath = "/auth"

  private val httpRoutes: HttpRoutes[F] =
    HttpRoutes.of[F]:
      case request @ POST -> Root / "users" =>
        request
          .decode[CreateUser]: user =>
            auth
              .createUser(user.userName.toDomain, user.password.toDomain)
              .flatMap(Created(_))
              .recoverWith:
                case UserNameInUse(u) => Conflict(u.show)

  val routes: HttpRoutes[F] = Router(
    prefixPath -> httpRoutes
  )

end UsersRoutes
