package com.kevchuang.shop.domain

import cats.derived.*
import cats.{Eq, Show}
import com.kevchuang.shop.domain.item.*
import io.circe.Encoder
import io.circe.generic.auto.*
import io.github.iltotore.iron.*
import io.github.iltotore.iron.cats.given
import io.github.iltotore.iron.circe.given
import io.github.iltotore.iron.constraint.all.*
import squants.Money
import squants.market.USD

import scala.concurrent.duration.FiniteDuration
import scala.util.control.NoStackTrace

object cart:

  opaque type ShoppingCartExpiration = FiniteDuration :| Pure
  object ShoppingCartExpiration
      extends RefinedTypeOps[FiniteDuration, Pure, ShoppingCartExpiration]

  final case class Cart(items: Map[ItemId, Quantity]) derives Eq, Show
  final case class CartItem(item: Item, quantity: Quantity) derives Eq, Show:
    def subTotal: Money = USD(item.price.amount * quantity.value)
  end CartItem

  final case class CartTotal(items: List[CartItem], total: Money)
      derives Eq,
        Show

  case object EmptyCartError extends NoStackTrace derives Show

  // codecs
  given Encoder[CartTotal] = Encoder.derived
  given Encoder[CartItem]  = Encoder.derived
end cart
