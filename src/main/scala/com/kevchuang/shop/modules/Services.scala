package com.kevchuang.shop.modules

import cats.effect.*
import cats.effect.kernel.Resource
import cats.effect.std.UUIDGen
import com.kevchuang.shop.services.Brands
import skunk.Session

sealed abstract class Services[F[_]] private (
    val brands: Brands[F]
)

object Services:
  def make[F[_]: UUIDGen: Temporal](
      postgres: Resource[F, Session[F]]
  ): Services[F] =
    new Services[F](
      brands = Brands.make[F](postgres)
    ) {}
