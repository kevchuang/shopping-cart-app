package com.kevchuang.shop.domain

import cats.*
import cats.derived.*
import com.kevchuang.shop.domain.brand.{Brand, BrandId}
import com.kevchuang.shop.domain.category.{Category, CategoryId}
import com.kevchuang.shop.domain.price.Price
import io.circe.*

import java.util.UUID

object item:
  opaque type ItemId = UUID
  object ItemId:
    def apply(uuid: UUID): ItemId = uuid

    extension (itemId: ItemId) def value: UUID = itemId

    given Decoder[ItemId] = Decoder.decodeUUID.map(ItemId(_))
    given Encoder[ItemId] = Encoder.encodeUUID
  end ItemId

  opaque type ItemName = String
  object ItemName:
    def apply(name: String): ItemName = name

    extension (itemName: ItemName) def value: String = itemName

    given Decoder[ItemName] = Decoder.decodeString.map(ItemName(_))
    given Encoder[ItemName] = Encoder.encodeString
  end ItemName

  opaque type ItemDescription = String
  object ItemDescription:
    def apply(description: String): ItemDescription = description

    extension (itemDescription: ItemDescription)
      def value: String = itemDescription

    given Decoder[ItemDescription] =
      Decoder.decodeString.map(ItemDescription(_))
    given Encoder[ItemDescription] = Encoder.encodeString
  end ItemDescription

  final case class Item(
      uuid: ItemId,
      name: ItemName,
      description: ItemDescription,
      price: Price,
      brand: Brand,
      category: Category
  ) derives Eq, Show

  final case class CreateItem(
      name: ItemName,
      description: ItemDescription,
      price: Price,
      brandId: BrandId,
      categoryId: CategoryId
  )
end item
