package com.kevchuang.shop.modules

import cats.effect.kernel.{Resource, Sync}
import com.kevchuang.shop.auth.{Crypto, JwtExpire, Tokens}
import com.kevchuang.shop.config.AppConfig
import com.kevchuang.shop.services.{Auth, Users}
import skunk.Session
import cats.syntax.all.*
import dev.profunktor.redis4cats.RedisCommands

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
      users = Users.make[F](postgres)
      auth  = Auth.make[F](crypto, redis, config.tokenExpiration, tokens, users)
    yield new Security[F](auth) {}
end Security

sealed abstract class Security[F[_]] private (
    val auth: Auth[F]
)
