package com.kevchuang.shop.modules

import cats.effect.Temporal
import cats.syntax.all.*
import com.kevchuang.shop.config.CheckoutConfig
import com.kevchuang.shop.effects.Background
import com.kevchuang.shop.programs.Checkout
import org.typelevel.log4cats.Logger
import retry.RetryPolicies.*
import retry.RetryPolicy

object Programs:
  def make[F[_]: Background: Logger: Temporal](
      config: CheckoutConfig,
      services: Services[F],
      clients: HttpClients[F]
  ): Programs[F] =
    new Programs[F](config, services, clients) {}
end Programs

sealed abstract class Programs[F[_]: Background: Logger: Temporal](
    config: CheckoutConfig,
    services: Services[F],
    clients: HttpClients[F]
):
  val retryPolicy: RetryPolicy[F] =
    limitRetries[F](config.retriesLimit.value) |+| exponentialBackoff[F](
      config.retriesBackoff
    )

  val checkout: Checkout[F] =
    Checkout(services.cart, services.orders, clients.payment, retryPolicy)
end Programs
