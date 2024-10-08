package com.kevchuang.shop.resources

import cats.effect.Resource
import cats.effect.kernel.{Concurrent, Temporal}
import cats.effect.std.Console
import cats.syntax.all.*
import com.kevchuang.shop.config.AppConfig
import com.kevchuang.shop.database.{PostgreSQL, RedisClient}
import dev.profunktor.redis4cats.RedisCommands
import dev.profunktor.redis4cats.effect.MkRedis
import fs2.io.net.Network
import org.http4s.client.Client
import org.typelevel.log4cats.Logger
import skunk.Session

sealed abstract class AppResources[F[_]] private (
    val client: Client[F],
    val postgres: Resource[F, Session[F]],
    val redis: RedisCommands[F, String, String]
)

object AppResources:
  def make[F[
      _
  ]: Concurrent: Console: Logger: MkHttpClient: MkRedis: Network: Temporal](
      appConfig: AppConfig
  ): Resource[F, AppResources[F]] =
    (
      MkHttpClient[F].newEmber(appConfig.httpClientConfig),
      PostgreSQL.make[F](appConfig.postgreSQLConfig),
      RedisClient.make[F](appConfig.redisConfig)
    ).parMapN(new AppResources[F](_, _, _) {})
end AppResources
