package com.kevchuang.shop.domain

import cats.{Eq, Show}
import cats.derived.*
import eu.timepit.refined.auto.*
import eu.timepit.refined.types.string.NonEmptyString
import io.circe.*
import org.http4s.{ParseFailure, QueryParamDecoder}

import java.util.UUID

object brand:

  opaque type BrandId = UUID
  object BrandId:
    def apply(uuid: UUID): BrandId = uuid

    extension (brandId: BrandId) def value: UUID = brandId

    given Decoder[BrandId] = Decoder.decodeUUID.map(BrandId(_))
    given Encoder[BrandId] = Encoder.encodeUUID
  end BrandId

  opaque type BrandName = String
  object BrandName:
    def apply(s: String): BrandName = s.toLowerCase.capitalize

    extension (brandName: BrandName) def value: String = brandName

    given Decoder[BrandName] = Decoder.decodeString.map(BrandName(_))
    given Encoder[BrandName] = Encoder.encodeString
  end BrandName

  opaque type BrandParam = NonEmptyString
  object BrandParam:
    def apply(s: NonEmptyString): BrandParam = s

    extension (brandParam: BrandParam)
      def toDomain: BrandName = BrandName(brandParam.toLowerCase.capitalize)

    given QueryParamDecoder[BrandParam] =
      QueryParamDecoder.stringQueryParamDecoder.emap(s =>
        NonEmptyString
          .from(s)
          .fold(
            e => Left(ParseFailure("brand decoder failure", e)),
            value => Right(BrandParam(value))
          )
      )
  end BrandParam

  final case class Brand(uuid: BrandId, name: BrandName) derives Eq, Show

end brand
