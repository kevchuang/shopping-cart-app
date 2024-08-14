package com.kevchuang.shop.modules

import cats.effect.MonadCancelThrow
import com.kevchuang.shop.config.PaymentConfig
import com.kevchuang.shop.http.clients.PaymentClient
import org.http4s.circe.JsonDecoder
import org.http4s.client.Client

sealed trait HttpClients[F[_]]:
  def payment: PaymentClient[F]
end HttpClients

object HttpClients:
  def make[F[_]: JsonDecoder: MonadCancelThrow](
      config: PaymentConfig,
      client: Client[F]
  ): HttpClients[F] =
    new HttpClients[F]:
      def payment: PaymentClient[F] = PaymentClient.make(config, client)
end HttpClients
