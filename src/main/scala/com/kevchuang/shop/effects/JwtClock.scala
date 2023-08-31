package com.kevchuang.shop.effects

import cats.effect.*

import java.time.Clock

trait JwtClock[F[_]]:
  def utc: F[Clock]
end JwtClock

object JwtClock:
  def apply[F[_]: JwtClock]: JwtClock[F] = implicitly

  given forSync[F[_]: Sync]: JwtClock[F] =
    new JwtClock[F]:
      override def utc: F[Clock] = Sync[F].delay(Clock.systemUTC)
end JwtClock
