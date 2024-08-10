package com.kevchuang.shop.domain

import com.kevchuang.shop.domain.item.*
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import squants.Money
import squants.market.USD

import scala.concurrent.duration.FiniteDuration

object cart:

  opaque type ShoppingCartExpiration = FiniteDuration :| Pure
  object ShoppingCartExpiration
      extends RefinedTypeOps[FiniteDuration, Pure, ShoppingCartExpiration]

  final case class Cart(items: Map[ItemId, Quantity])
  final case class CartItem(item: Item, quantity: Quantity):
    def subTotal: Money = USD(item.price.amount * quantity.value)
  end CartItem

  final case class CartTotal(items: List[CartItem], total: Money)
end cart
