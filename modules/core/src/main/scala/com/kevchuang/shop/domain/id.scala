package com.kevchuang.shop.domain

import cats.effect.std.UUIDGen
import cats.syntax.all.*
import cats.{Functor, MonadThrow}

import java.util.UUID

object id:
  object ID:
    def fromStr[F[_], A](
        id: String
    )(toId: UUID => A)(using F: MonadThrow[F]): F[A] =
      F.fromEither(
        Either.catchNonFatal(toId(UUID.fromString(id)))
      )

    def make[F[_]: Functor, A](f: UUID => A)(using gen: UUIDGen[F]): F[A] =
      gen.randomUUID.map(f)
  end ID
end id
