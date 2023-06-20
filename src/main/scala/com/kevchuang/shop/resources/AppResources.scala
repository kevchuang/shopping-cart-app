package com.kevchuang.shop.resources

import cats.effect.Resource
import cats.effect.kernel.{Concurrent, Temporal}
import cats.effect.std.Console
import com.kevchuang.shop.config.AppConfig
import com.kevchuang.shop.database.PostgreSQL
import fs2.io.net.Network
import org.typelevel.log4cats.Logger
import skunk.Session

sealed abstract class AppResources[F[_]] private (
    val postgres: Resource[F, Session[F]]
)

object AppResources:
  def make[F[_]: Concurrent: Console: Logger: Network: Temporal](
      appConfig: AppConfig
  ): Resource[F, AppResources[F]] =
    PostgreSQL
      .make[F](appConfig.postgreSQLConfig)
      .map(new AppResources[F](_) {})
