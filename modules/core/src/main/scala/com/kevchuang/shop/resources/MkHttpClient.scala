package com.kevchuang.shop.resources

import cats.effect.Resource
import cats.effect.kernel.Async
import com.kevchuang.shop.config.HttpClientConfig
import fs2.io.net.Network
import org.http4s.client.Client
import org.http4s.ember.client.EmberClientBuilder

trait MkHttpClient[F[_]]:
  def newEmber(config: HttpClientConfig): Resource[F, Client[F]]
end MkHttpClient

object MkHttpClient:
  def apply[F[_]: MkHttpClient]: MkHttpClient[F] = implicitly

  implicit def forAsync[F[_]: Async: Network]: MkHttpClient[F] =
    new MkHttpClient[F]:
      def newEmber(c: HttpClientConfig): Resource[F, Client[F]] =
        EmberClientBuilder
          .default[F]
          .withTimeout(c.timeout)
          .withIdleTimeInPool(c.idleTimeInPool)
          .build

end MkHttpClient
