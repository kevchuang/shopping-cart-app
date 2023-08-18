package com.kevchuang.shop.domain

import cats.*
import cats.derived.*
import com.kevchuang.shop.domain.brand.{Brand, BrandId}
import com.kevchuang.shop.domain.category.{Category, CategoryId}
import com.kevchuang.shop.domain.price.Price
import com.kevchuang.shop.domain.types.common.NotEmpty
import io.github.iltotore.iron.*
import io.github.iltotore.iron.cats.given
import io.github.iltotore.iron.constraint.all.*

import java.util.UUID

object item:
  opaque type ItemId = UUID :| Pure
  object ItemId extends RefinedTypeOpsImpl[UUID, Pure, ItemId]

  opaque type ItemName = String :| NotEmpty
  object ItemName extends RefinedTypeOpsImpl[String, NotEmpty, ItemName]

  opaque type ItemDescription = String :| Pure
  object ItemDescription
      extends RefinedTypeOpsImpl[String, Pure, ItemDescription]

  final case class Item(
      uuid: ItemId,
      name: ItemName,
      description: ItemDescription,
      price: Price,
      brand: Brand,
      category: Category
  ) derives Eq,
        Show

  final case class CreateItem(
      name: ItemName,
      description: ItemDescription,
      price: Price,
      brandId: BrandId,
      categoryId: CategoryId
  )
end item
