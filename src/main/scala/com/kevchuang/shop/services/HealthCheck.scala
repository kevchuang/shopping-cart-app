package com.kevchuang.shop.services

import cats.effect.Resource
import cats.effect.implicits.*
import cats.effect.kernel.Temporal
import cats.syntax.all.*
import com.kevchuang.shop.domain.health.*
import com.kevchuang.shop.domain.health.Status.*
import dev.profunktor.redis4cats.RedisCommands
import skunk.*
import skunk.codec.all.int4
import skunk.implicits.*

import scala.concurrent.duration.*

trait HealthCheck[F[_]]:
  def status: F[AppStatus]
end HealthCheck

object HealthCheck:
  def make[F[_]: Temporal](
      postgres: Resource[F, Session[F]],
      redis: RedisCommands[F, String, String]
  ): HealthCheck[F] =
    new HealthCheck[F]:

      val query: Query[Void, Int] =
        sql"SELECT pid FROM pg_stat_activity".query(int4)

      val postgresStatus: F[PostgresStatus] =
        postgres
          .use(_.execute(query))
          .map(_.nonEmpty)
          .timeout(1.second)
          .map(boolToStatus)
          .orElse(Unreachable.pure[F])
          .map(PostgresStatus.apply)

      val redisStatus: F[RedisStatus] =
        redis.ping
          .map(_.nonEmpty)
          .timeout(1.second)
          .map(boolToStatus)
          .orElse(Unreachable.pure[F])
          .map(RedisStatus.apply)

      def status: F[AppStatus] =
        (postgresStatus, redisStatus).parMapN(AppStatus.apply)
end HealthCheck
