package com.kevchuang.shop.services

import cats.effect.kernel.{MonadCancelThrow, Resource}
import com.kevchuang.shop.domain.brand.*
import com.kevchuang.shop.sql.codecs.*
import skunk.*
import skunk.implicits.*

trait Brands[F[_]]:
  def findAll: F[List[Brand]]
  def create(name: BrandName): F[BrandId]

object Brands:
  def make[F[_]: MonadCancelThrow](
      postgres: Resource[F, Session[F]]
  ): Brands[F] =
    import BrandsSQL.*
    new Brands[F]:
      def findAll: F[List[Brand]] = postgres.use(_.execute(selectBrands))

      def create(name: BrandName): F[BrandId] = ???

private object BrandsSQL:
  private val brandDecoder: Decoder[Brand] = (brandId ~ brandName).map {
    case (brandId, brandName) => Brand(brandId, brandName)
  }

  val selectBrands: Query[Void, Brand] =
    sql"SELECT * FROM brands".query(brandDecoder)
