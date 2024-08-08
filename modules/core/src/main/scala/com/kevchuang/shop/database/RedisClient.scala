package com.kevchuang.shop.database

import cats.effect.*
import cats.syntax.all.*
import com.kevchuang.shop.config.RedisConfig
import dev.profunktor.redis4cats.effect.*
import dev.profunktor.redis4cats.{Redis, RedisCommands}
import org.typelevel.log4cats.Logger

object RedisClient:
  def make[F[_]: Concurrent: Logger: MkRedis](
      config: RedisConfig
  ): Resource[F, RedisCommands[F, String, String]] =
    def checkRedisConnection(
        redis: RedisCommands[F, String, String]
    ): F[Unit] =
      redis.info.flatMap:
        _.get("redis_version").traverse_ : version =>
          Logger[F].info(s"Connected to redis $version")
    end checkRedisConnection

    Redis[F]
      .utf8(config.uri.value)
      .evalTap(checkRedisConnection)

end RedisClient
