package com.kevchuang.shop.http.clients

import cats.effect.MonadCancelThrow
import cats.syntax.all.*
import com.kevchuang.shop.config.PaymentConfig
import com.kevchuang.shop.domain.given
import com.kevchuang.shop.domain.order.PaymentError
import com.kevchuang.shop.domain.payment.{Payment, PaymentId}
import io.circe.generic.auto.*
import io.github.iltotore.iron.*
import io.github.iltotore.iron.circe.given
import org.http4s.Method.*
import org.http4s.circe.*
import org.http4s.circe.CirceEntityEncoder.*
import org.http4s.client.Client
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.{Status, Uri}

trait PaymentClient[F[_]]:
  def process(payment: Payment): F[PaymentId]
end PaymentClient

object PaymentClient:
  def make[F[_]: JsonDecoder: MonadCancelThrow](
      config: PaymentConfig,
      client: Client[F]
  ): PaymentClient[F] =
    new PaymentClient[F] with Http4sClientDsl[F]:
      def process(payment: Payment): F[PaymentId] =
        Uri
          .fromString(config.uri.value + "/payments")
          .liftTo[F]
          .flatMap: uri =>
            client
              .run(POST(payment, uri))
              .use: resp =>
                resp.status match
                  case Status.Ok | Status.Conflict =>
                    resp.asJsonDecode[PaymentId]
                  case st =>
                    PaymentError(
                      Option(st.reason).getOrElse("unknown")
                    ).raiseError[F, PaymentId]

end PaymentClient
