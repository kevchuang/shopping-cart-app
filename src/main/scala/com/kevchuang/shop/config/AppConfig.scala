package com.kevchuang.shop.config

import cats.effect.kernel.Async
import ciris.*
import com.comcast.ip4s.*
import com.kevchuang.shop.config.PostgreSQLConfig.*
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import io.github.iltotore.iron.cats.given
import com.kevchuang.shop.macros.ciris.given

final case class AppConfig(
    httpServerConfig: HttpServerConfig,
    postgreSQLConfig: PostgreSQLConfig
)

object AppConfig:
  def load[F[_]: Async]: F[AppConfig] = default[F].load[F]

  private def default[F[_]]: ConfigValue[F, AppConfig] =
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
        )
      )
    }
