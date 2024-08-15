package com.kevchuang.shop.programs

import cats.MonadThrow
import cats.data.NonEmptyList
import cats.syntax.all.*
import com.kevchuang.shop.domain.auth.UserId
import com.kevchuang.shop.domain.cart.{CartItem, EmptyCartError}
import com.kevchuang.shop.domain.checkout.Card
import com.kevchuang.shop.domain.order.*
import com.kevchuang.shop.domain.payment.{Payment, PaymentId}
import com.kevchuang.shop.effects.Background
import com.kevchuang.shop.http.clients.PaymentClient
import com.kevchuang.shop.retries.{Retriable, Retry}
import com.kevchuang.shop.services.{Orders, ShoppingCart}
import io.github.iltotore.iron.*
import io.github.iltotore.iron.cats.given
import org.typelevel.log4cats.Logger
import retry.RetryPolicy
import squants.Money

import scala.concurrent.duration.*

final case class Checkout[F[_]: MonadThrow: Background: Retry: Logger](
    cart: ShoppingCart[F],
    orders: Orders[F],
    payment: PaymentClient[F],
    retryPolicy: RetryPolicy[F]
                                                                      ):
  private def ensureNonEmptyCart(
      items: List[CartItem]
  ): F[NonEmptyList[CartItem]] =
    MonadThrow[F].fromOption(NonEmptyList.fromList(items), EmptyCartError)

  private def processPayment(p: Payment): F[PaymentId] =
    Retry[F]
      .retry(retryPolicy, Retriable.Payment)(
        payment.process(p)
      )
      .adaptError:
        case e => PaymentError(Option(e.getMessage).getOrElse("unknown"))

  private def createOrder(
      userId: UserId,
      paymentId: PaymentId,
      items: NonEmptyList[CartItem],
      total: Money
  ): F[OrderId] =
    val action =
      Retry[F]
        .retry(retryPolicy, Retriable.Orders)(
          orders.create(userId, paymentId, items, total)
        )
        .adaptError:
          case e => OrderError(Option(e.getMessage).getOrElse("unknown"))

    def bgAction(fa: F[OrderId]): F[OrderId] =
      fa.onError:
        case _ =>
          Logger[F].error(s"Failed to create order for: ${paymentId.show}") *>
            Background[F].schedule(bgAction(action), 1.hour)

    bgAction(action)

  def process(userId: UserId, card: Card): F[OrderId] =
    for
      c         <- cart.get(userId)
      items     <- ensureNonEmptyCart(c.items)
      paymentId <- processPayment(Payment(userId, c.total, card))
      orderId   <- createOrder(userId, paymentId, items, c.total)
      _         <- cart.delete(userId).attempt.void
    yield orderId
end Checkout
