package com.kevchuang.shop.config

import cats.effect.*
import ciris.*
import com.comcast.ip4s.*
import com.kevchuang.shop.config.PostgreSQLConfig.*
import com.kevchuang.shop.config.RedisConfig.RedisURI
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import io.github.iltotore.iron.cats.given
import com.kevchuang.shop.macros.ciris.given

final case class AppConfig(
    httpServerConfig: HttpServerConfig,
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
      env("SC_POSTGRES_PASSWORD").as[Password].secret
    ).map { (postgrePassword) =>
      AppConfig(
        HttpServerConfig(
          host = host"0.0.0.0",
          port = port"8080"
        ),
        PostgreSQLConfig(
          host = HostName("localhost"),
          port = PortNumber(5432),
          user = UserName("postgres"),
          password = postgrePassword,
          database = DatabaseName("store"),
          max = 10
        ),
        RedisConfig(
          uri = redisURI
        )
      )
    }
