package com.kevchuang.shop.domain

import io.circe.{Decoder, Encoder, Json}

import java.util.UUID

object brand:

  opaque type BrandId = UUID
  object BrandId:
    def apply(uuid: UUID): BrandId = uuid

    given Decoder[BrandId] = Decoder.decodeUUID.map(BrandId(_))
    given Encoder[BrandId] = Encoder.encodeUUID
  end BrandId

  extension (brandId: BrandId) def value: UUID = brandId

  opaque type BrandName = String
  object BrandName:
    def apply(s: String): BrandName = s.toLowerCase.capitalize

    given brandNameDecoder: Decoder[BrandName] =
      Decoder.decodeString.map(BrandName(_))
    given brandNameEncoder: Encoder[BrandName] = (brandName: BrandName) =>
      Json.fromString(brandName)
  end BrandName

  final case class Brand(uuid: BrandId, name: BrandName)

end brand
