package com.kevchuang.shop.config

import com.kevchuang.shop.config.types.RetryLimit

import scala.concurrent.duration.FiniteDuration

final case class CheckoutConfig(
    retriesLimit: RetryLimit,
    retriesBackoff: FiniteDuration
)
