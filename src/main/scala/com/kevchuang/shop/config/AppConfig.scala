package com.kevchuang.shop.config

import cats.syntax.parallel.*
import cats.effect.*
import ciris.*
import com.comcast.ip4s.*
import com.kevchuang.shop.config.PostgreSQLConfig.*
import com.kevchuang.shop.config.RedisConfig.RedisURI
import com.kevchuang.shop.config.types.*
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import io.github.iltotore.iron.cats.given
import io.github.iltotore.iron.ciris.given
import scala.concurrent.duration.*

final case class AppConfig(
    httpServerConfig: HttpServerConfig,
    tokenConfig: Secret[JwtAccessTokenKeyConfig],
    passwordSalt: Secret[PasswordSalt],
    tokenExpiration: TokenExpiration,
    postgreSQLConfig: PostgreSQLConfig,
    redisConfig: RedisConfig
)

object AppConfig:
  def load[F[_]: Async]: F[AppConfig] =
    default[F](RedisURI("redis://localhost")).load[F]

  private def default[F[_]](
      redisURI: RedisURI
  ): ConfigValue[F, AppConfig] =
    (
      env("SC_ACCESS_TOKEN_SECRET_KEY").as[JwtAccessTokenKeyConfig].secret,
      env("SC_PASSWORD_SALT").as[PasswordSalt].secret,
      env("SC_POSTGRES_PASSWORD").as[Password].secret
    ).parMapN { (accessToken, passwordSalt, postgresPassword) =>
      AppConfig(
        HttpServerConfig(
          host = host"0.0.0.0",
          port = port"8080"
        ),
        accessToken,
        passwordSalt,
        TokenExpiration(30.minutes),
        PostgreSQLConfig(
          host = HostName("localhost"),
          port = PortNumber(5432),
          user = UserName("postgres"),
          password = postgresPassword,
          database = DatabaseName("store"),
          max = 10
        ),
        RedisConfig(
          uri = redisURI
        )
      )
    }
