package com.kevchuang.shop.domain

import cats.derived.*
import cats.{Eq, Show}
import io.circe.{Decoder, Encoder}

import java.util.UUID

object category:
  opaque type CategoryId = UUID
  object CategoryId:
    def apply(uuid: UUID): CategoryId = uuid

    extension (categoryId: CategoryId) def value: UUID = categoryId

    given Decoder[CategoryId] = Decoder.decodeUUID.map(CategoryId.apply)
    given Encoder[CategoryId] = Encoder.encodeUUID
  end CategoryId

  opaque type CategoryName = String
  object CategoryName:
    def apply(name: String): CategoryName = name

    extension (categoryName: CategoryName) def value: String = categoryName

    given Decoder[CategoryName] = Decoder.decodeString.map(CategoryName.apply)
    given Encoder[CategoryName] = Encoder.encodeString
  end CategoryName

  final case class Category(uuid: CategoryId, name: CategoryName)
      derives Eq,
        Show
end category
