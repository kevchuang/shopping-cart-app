package com.kevchuang.shop.domain

import cats.*
import cats.derived.*
import com.kevchuang.shop.domain.brand.{Brand, BrandId}
import com.kevchuang.shop.domain.cart.CartItem
import com.kevchuang.shop.domain.category.{Category, CategoryId}
import com.kevchuang.shop.domain.types.common.NotEmpty
import io.github.iltotore.iron.*
import io.github.iltotore.iron.cats.given
import io.github.iltotore.iron.constraint.all.*
import squants.Money

import java.util.UUID

object item:
  opaque type Quantity = Int :| Positive
  object Quantity extends RefinedTypeOps[Int, Positive, Quantity]

  opaque type ItemId = UUID :| Pure
  object ItemId extends RefinedTypeOps[UUID, Pure, ItemId]

  opaque type ItemName = String :| NotEmpty
  object ItemName extends RefinedTypeOps[String, NotEmpty, ItemName]

  opaque type ItemDescription = String :| Pure
  object ItemDescription extends RefinedTypeOps[String, Pure, ItemDescription]

  final case class Item(
      uuid: ItemId,
      name: ItemName,
      description: ItemDescription,
      price: Money,
      brand: Brand,
      category: Category
  ) derives Eq,
        Show:
    def cart(quantity: Quantity): CartItem =
      CartItem(this, quantity)
  end Item

  final case class CreateItem(
      name: ItemName,
      description: ItemDescription,
      price: Money,
      brandId: BrandId,
      categoryId: CategoryId
  )

  final case class UpdateItem(
      id: ItemId,
      price: Money
  )
end item
