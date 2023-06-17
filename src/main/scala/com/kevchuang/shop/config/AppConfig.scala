package com.kevchuang.shop.config

import cats.effect.kernel.Async
import ciris.ConfigValue

final case class AppConfig(
    httpServerConfig: HttpServerConfig,
    postgreSQLConfig: PostgreSQLConfig
)

object AppConfig:
  def load[F[_]: Async]: F[AppConfig] =
    default[F]
      .load[F]

  private def default[F[_]]: ConfigValue[F, AppConfig] = ???
