package com.kevchuang.shop.domain

import scala.util.control.NoStackTrace

object order:
  sealed trait OrderOrPaymentError extends NoStackTrace:
    def cause: String
  end OrderOrPaymentError

  final case class PaymentError(cause: String) extends OrderOrPaymentError

end order
