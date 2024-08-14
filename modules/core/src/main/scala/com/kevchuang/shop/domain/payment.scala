package com.kevchuang.shop.domain

import cats.derived.*
import cats.{Eq, Show}
import com.kevchuang.shop.domain.auth.UserId
import com.kevchuang.shop.domain.checkout.Card
import com.kevchuang.shop.domain.given
import io.github.iltotore.iron.*
import io.github.iltotore.iron.cats.given
import squants.market.Money

import java.util.UUID

object payment:
  opaque type PaymentId = UUID :| Pure
  object PaymentId extends RefinedTypeOps[UUID, Pure, PaymentId]

  final case class Payment(
      id: UserId,
      total: Money,
      card: Card
  ) derives Eq,
        Show
end payment
