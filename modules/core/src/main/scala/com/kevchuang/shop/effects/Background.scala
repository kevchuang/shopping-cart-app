package com.kevchuang.shop.effects

import cats.effect.Temporal
import cats.effect.std.Supervisor
import cats.syntax.all.*

import scala.concurrent.duration.FiniteDuration

trait Background[F[_]]:
  def schedule[A](fa: F[A], duration: FiniteDuration): F[Unit]
end Background

object Background:
  def apply[F[_]: Background]: Background[F] = implicitly

  implicit def bgInstance[F[_]](using
      S: Supervisor[F],
      T: Temporal[F]
  ): Background[F] =
    new Background[F]:
      def schedule[A](
          fa: F[A],
          duration: FiniteDuration
      ): F[Unit] =
        S.supervise(T.sleep(duration) *> fa).void

end Background
