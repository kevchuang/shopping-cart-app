package com.kevchuang.shop.config

import cats.effect.kernel.Async
import ciris.*
import ciris.refined.*
import com.comcast.ip4s.*
import eu.timepit.refined.*
import eu.timepit.refined.auto.*
import eu.timepit.refined.cats.*
import eu.timepit.refined.types.string.*
import cats.syntax.*
import eu.timepit.refined.api.Refined
import eu.timepit.refined.numeric.*

final case class AppConfig(
    httpServerConfig: HttpServerConfig,
    postgreSQLConfig: PostgreSQLConfig
)

object AppConfig:
  def load[F[_]: Async]: F[AppConfig] =
    default[F]
      .load[F]

  private def default[F[_]]: ConfigValue[F, AppConfig] =
    (
      env("SC_POSTGRES_PASSWORD").as[NonEmptyString].secret
    ).map { (postgrePassword) =>
      AppConfig(
        HttpServerConfig(
          host = host"0.0.0.0",
          port = port"8080"
        ),
        PostgreSQLConfig(
          host = "localhost",
          port = 5432,
          user = "postgres",
          password = postgrePassword,
          database = "store",
          max = 10
        )
      )
    }
