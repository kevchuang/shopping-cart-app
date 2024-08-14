package com.kevchuang.shop.modules

import cats.effect.*
import cats.effect.kernel.Resource
import cats.effect.std.UUIDGen
import com.kevchuang.shop.domain.cart.ShoppingCartExpiration
import com.kevchuang.shop.services.*
import dev.profunktor.redis4cats.RedisCommands
import skunk.Session

sealed abstract class Services[F[_]] private (
    val brands: Brands[F],
    val cart: ShoppingCart[F],
    val categories: Categories[F],
    val healthCheck: HealthCheck[F],
    val items: Items[F],
    val orders: Orders[F]
)

object Services:
  def make[F[_]: UUIDGen: Temporal](
      postgres: Resource[F, Session[F]],
      redis: RedisCommands[F, String, String],
      cartExpiration: ShoppingCartExpiration
  ): Services[F] =
    val items = Items.make[F](postgres)
    new Services[F](
      brands = Brands.make[F](postgres),
      cart = ShoppingCart.make[F](items, redis, cartExpiration),
      categories = Categories.make[F](postgres),
      healthCheck = HealthCheck.make[F](postgres, redis),
      items = items,
      orders = Orders.make[F](postgres)
    ) {}
