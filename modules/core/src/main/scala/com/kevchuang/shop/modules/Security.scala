package com.kevchuang.shop.modules

import cats.effect.{Resource, Sync}
import cats.syntax.all.*
import com.kevchuang.shop.auth.{Crypto, JwtExpire, Tokens}
import com.kevchuang.shop.config.AppConfig
import com.kevchuang.shop.http.auth.users.{CommonUser, UserJwtAuth}
import com.kevchuang.shop.services.{Auth, Users, UsersAuth}
import dev.profunktor.auth.jwt.JwtAuth
import dev.profunktor.redis4cats.RedisCommands
import io.github.iltotore.iron.*
import pdi.jwt.JwtAlgorithm
import skunk.Session

object Security:
  def make[F[_]: Sync](
      config: AppConfig,
      postgres: Resource[F, Session[F]],
      redis: RedisCommands[F, String, String]
  ): F[Security[F]] =
    for
      crypto <- Crypto.make[F](config.passwordSalt.value)
      tokens <-
        JwtExpire
          .make[F]
          .map(
            Tokens
              .make[F](_, config.tokenConfig.value, config.tokenExpiration)
          )
      users     = Users.make[F](postgres)
      usersAuth = UsersAuth.common[F](redis)
      userJwtAuth = UserJwtAuth(
                      JwtAuth.hmac(
                        config.tokenConfig.value.value,
                        JwtAlgorithm.HS256
                      )
                    )
      auth = Auth.make[F](crypto, redis, config.tokenExpiration, tokens, users)
    yield new Security[F](auth, usersAuth, userJwtAuth) {}
end Security

sealed abstract class Security[F[_]] private (
    val auth: Auth[F],
    val userAuth: UsersAuth[F, CommonUser],
    val userJwtAuth: UserJwtAuth
)
