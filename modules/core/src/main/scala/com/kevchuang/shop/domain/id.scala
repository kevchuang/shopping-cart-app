package com.kevchuang.shop.domain

import cats.effect.Sync
import cats.syntax.all.*

import java.util.UUID

object id:
  object ID:
    def fromStr[F[_], A](
        id: String
    )(toId: UUID => A)(using F: Sync[F]): F[A] =
      F.delay(UUID.fromString(id))
        .attempt
        .flatMap:
          case Left(e)   => e.raiseError[F, A]
          case Right(id) => toId(id).pure[F]

    def make[F[_], A](f: UUID => A)(using F: Sync[F]): F[A] =
      F.delay(UUID.randomUUID())
        .attempt
        .flatMap:
          case Left(e)     => e.raiseError[F, A]
          case Right(uuid) => f(uuid).pure[F]
  end ID
end id
