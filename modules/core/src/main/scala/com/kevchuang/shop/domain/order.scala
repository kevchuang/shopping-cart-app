package com.kevchuang.shop.domain

import com.kevchuang.shop.domain.item.{ItemId, Quantity}
import com.kevchuang.shop.domain.payment.PaymentId
import io.github.iltotore.iron.*
import squants.market.Money

import java.util.UUID
import scala.util.control.NoStackTrace

object order:

  opaque type OrderId = UUID :| Pure
  object OrderId extends RefinedTypeOps[UUID, Pure, OrderId]

  final case class Order(
      id: OrderId,
      paymentId: PaymentId,
      items: Map[ItemId, Quantity],
      total: Money
  )

  sealed trait OrderOrPaymentError extends NoStackTrace:
    def cause: String
  end OrderOrPaymentError

  final case class PaymentError(cause: String) extends OrderOrPaymentError

end order
