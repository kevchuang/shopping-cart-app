package com.kevchuang.shop.database

import cats.effect.Resource
import cats.effect.kernel.*
import cats.effect.std.Console
import cats.syntax.all.*
import com.kevchuang.shop.config.PostgreSQLConfig
import fs2.io.net.Network
import org.typelevel.log4cats.Logger
import skunk.*
import skunk.codec.text.*
import skunk.implicits.*
import natchez.Trace.Implicits.noop

object PostgreSQL:
  def make[F[_]: Concurrent: Console: Logger: Network: Temporal](
      config: PostgreSQLConfig
  ): SessionPool[F] =
    def checkPostgresConnection(
        postgres: Resource[F, Session[F]]
    ): F[Unit] =
      postgres.use: session =>
        session
          .unique(sql"select version();".query(text))
          .flatMap: v =>
            Logger[F].info(s"Connected to Postgres $v")

    Session
      .pooled[F](
        host = config.host,
        port = config.port,
        user = config.user,
        password = Some(config.password.value),
        database = config.database,
        max = config.max
      )
      .evalTap(checkPostgresConnection)
end PostgreSQL
