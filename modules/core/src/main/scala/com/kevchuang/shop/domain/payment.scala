package com.kevchuang.shop.domain

import com.kevchuang.shop.domain.auth.UserId
import com.kevchuang.shop.domain.checkout.Card
import squants.market.Money

object payment:
  final case class Payment(
      id: UserId,
      total: Money,
      card: Card
  )
end payment
