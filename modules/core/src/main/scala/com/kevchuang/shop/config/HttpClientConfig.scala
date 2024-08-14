package com.kevchuang.shop.config

import scala.concurrent.duration.FiniteDuration

final case class HttpClientConfig(
    timeout: FiniteDuration,
    idleTimeInPool: FiniteDuration
)
