package com.kevchuang.shop.domain

import io.circe.*

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

  final case class Brand(uuid: BrandId, name: BrandName)

end brand
