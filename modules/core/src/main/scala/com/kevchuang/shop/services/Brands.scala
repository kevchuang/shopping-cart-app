package com.kevchuang.shop.services

import cats.effect.*
import cats.effect.kernel.{MonadCancelThrow, Resource}
import cats.effect.std.UUIDGen
import cats.syntax.all.*
import com.kevchuang.shop.domain.brand.*
import com.kevchuang.shop.sql.codecs.*
import skunk.*
import skunk.implicits.*
import io.github.iltotore.iron.*

trait Brands[F[_]]:
  def findAll: F[List[Brand]]
  def create(name: BrandName): F[BrandId]

object Brands:
  def make[F[_]: UUIDGen: MonadCancelThrow](
      postgres: Resource[F, Session[F]]
  ): Brands[F] =
    import BrandsSQL.*
    new Brands[F]:
      def findAll: F[List[Brand]] = postgres.use(_.execute(selectBrands))

      def create(name: BrandName): F[BrandId] =
        postgres.use: session =>
          for
            preparedCommand <- session.prepare(insertBrand)
            brandId         <- UUIDGen.randomUUID[F].map(BrandId(_))
            _               <- preparedCommand.execute(Brand(brandId, name))
          yield brandId
end Brands

private object BrandsSQL:
  private val brandCodec: Codec[Brand] = (brandId ~ brandName).imap {
    case (brandId, brandName) => Brand(brandId, brandName)
  }(brand => (brand.uuid, brand.name))

  val selectBrands: Query[Void, Brand] =
    sql"SELECT * FROM brands".query(brandCodec)

  val insertBrand: Command[Brand] =
    sql"INSERT INTO brands VALUES ($brandCodec)".command
