package com.kevchuang.shop.domain

import io.circe.Encoder

object health:
  sealed trait Status
  object Status:
    case object Okay        extends Status
    case object Unreachable extends Status

    def boolToStatus(boolean: Boolean): Status =
      if boolean then Okay else Unreachable

    given Encoder[Status] = Encoder.encodeString.contramap(_.toString)
  end Status

  final case class PostgresStatus(status: Status)
  final case class RedisStatus(status: Status)
  final case class AppStatus(postgres: PostgresStatus, redis: RedisStatus)
end health
