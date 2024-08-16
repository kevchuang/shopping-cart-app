package com.kevchuang.shop.programs

import cats.data.NonEmptyList
import cats.effect.IO
import cats.effect.kernel.Ref
import cats.syntax.all.*
import com.kevchuang.shop.domain.auth.*
import com.kevchuang.shop.domain.cart.*
import com.kevchuang.shop.domain.checkout.*
import com.kevchuang.shop.domain.item.*
import com.kevchuang.shop.domain.order.*
import com.kevchuang.shop.domain.payment.*
import com.kevchuang.shop.effects.{Background, TestBackground}
import com.kevchuang.shop.http.clients.PaymentClient
import com.kevchuang.shop.retries.{Retry, TestRetry}
import com.kevchuang.shop.services.{Orders, ShoppingCart}
import com.kevchuang.shop.utils.generators.*
import io.github.iltotore.iron.cats.given
import org.scalacheck.Gen
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.noop.NoOpLogger
import retry.RetryDetails.{GivingUp, WillDelayAndRetry}
import retry.RetryPolicies.limitRetries
import retry.RetryPolicy
import squants.Money
import squants.market.USD
import weaver.SimpleIOSuite
import weaver.scalacheck.Checkers

import scala.concurrent.duration.*
import scala.util.control.NoStackTrace

object CheckoutSuite extends SimpleIOSuite with Checkers:
  val maxRetries: Int              = 3
  val retryPolicy: RetryPolicy[IO] = limitRetries[IO](maxRetries)

  def successfulPayment(paymentId: PaymentId): PaymentClient[IO] =
    new PaymentClient[IO]:
      def process(payment: Payment): IO[PaymentId] = paymentId.pure[IO]

  val failingPayment: PaymentClient[IO] =
    new PaymentClient[IO]:
      def process(payment: Payment): IO[PaymentId] = PaymentError("").raiseError

  def recoveringPayment(
      attemptsSoFar: Ref[IO, Int],
      paymentId: PaymentId
  ): PaymentClient[IO] =
    new PaymentClient[IO]:
      def process(payment: Payment): IO[PaymentId] =
        attemptsSoFar.get.flatMap:
          case n if n === 1 => IO.pure(paymentId)
          case _ =>
            attemptsSoFar.update(_ + 1) *> IO.raiseError(PaymentError(""))

  val failingOrders: Orders[IO] =
    new TestOrders:
      override def create(
          userId: UserId,
          paymentId: PaymentId,
          items: NonEmptyList[CartItem],
          total: Money
      ): IO[OrderId] = OrderError("").raiseError

  val emptyCart: ShoppingCart[IO] =
    new TestCart:
      override def get(userId: UserId): IO[CartTotal] =
        CartTotal(List.empty, USD(0)).pure[IO]

  def failingCart(cart: CartTotal): ShoppingCart[IO] =
    new TestCart:
      override def get(userId: UserId): IO[CartTotal] =
        cart.pure[IO]
      override def delete(userId: UserId): IO[Unit] =
        IO.raiseError(new NoStackTrace {})

  def successfulCart(cart: CartTotal): ShoppingCart[IO] =
    new TestCart:
      override def get(userId: UserId): IO[CartTotal] =
        cart.pure[IO]
      override def delete(userId: UserId): IO[Unit] =
        IO.unit

  def successfulOrders(orderId: OrderId): Orders[IO] =
    new TestOrders:
      override def create(
          userId: UserId,
          paymentId: PaymentId,
          items: NonEmptyList[CartItem],
          total: Money
      ): IO[OrderId] =
        orderId.pure[IO]

  val gen: Gen[(UserId, OrderId, PaymentId, CartTotal, Card)] =
    for
      uid   <- userIdGen
      oid   <- orderIdGen
      pid   <- paymentIdGen
      total <- cartTotalGen
      card  <- cardGen
    yield (uid, oid, pid, total, card)

  given Background[IO] = TestBackground.NoOp
  given Logger[IO]     = NoOpLogger[IO]

  test("empty cart"):
    forall(gen) { (uid, oid, pid, _, card) =>
      Checkout[IO](
        emptyCart,
        successfulOrders(oid),
        successfulPayment(pid),
        retryPolicy
      ).process(uid, card)
        .attempt
        .map:
          case Left(EmptyCartError) => success
          case _                    => failure("Cart was not empty as expected ")
    }

  test("unreachable payment client"):
    forall(gen) { (uid, oid, _, cart, card) =>
      Ref.of[IO, Option[GivingUp]](None).flatMap { retries =>
        given Retry[IO] = TestRetry.givingUp(retries)

        Checkout[IO](
          successfulCart(cart),
          successfulOrders(oid),
          failingPayment,
          retryPolicy
        )
          .process(uid, card)
          .attempt
          .flatMap:
            case Left(PaymentError(_)) =>
              retries.get.map:
                case Some(g) => expect.same(g.totalRetries, maxRetries)
                case None    => failure("expected GivingUp")
            case _ => IO.pure(failure("Expected payment error"))
      }
    }

  test("failing payment client succeeds after one retry"):
    forall(gen) { (uid, oid, pid, cart, card) =>
      (Ref.of[IO, Option[WillDelayAndRetry]](None), Ref.of[IO, Int](0)).tupled
        .flatMap:
          case (retries, cliRef) =>
            given Retry[IO] = TestRetry.recovering(retries)

            Checkout[IO](
              successfulCart(cart),
              successfulOrders(oid),
              recoveringPayment(cliRef, pid),
              retryPolicy
            )
              .process(uid, card)
              .attempt
              .flatMap:
                case Right(id) =>
                  retries.get.map:
                    case Some(w) =>
                      expect.same(id, oid) |+| expect.same(w.retriesSoFar, 0)
                    case None => failure("Expect one retry")
                case _ => IO.pure(failure("Expecting success after one retry"))
    }

  test("cannot create order, run in the background"):
    forall(gen) { (uid, _, pid, cart, card) =>
      (
        Ref.of[IO, (Int, FiniteDuration)](0 -> 0.seconds),
        Ref.of[IO, Option[GivingUp]](None)
      ).tupled.flatMap: (bgActions, retries) =>
        given Background[IO] = TestBackground.counter(bgActions)
        given Retry[IO]      = TestRetry.givingUp(retries)

        Checkout[IO](
          successfulCart(cart),
          failingOrders,
          successfulPayment(pid),
          retryPolicy
        )
          .process(uid, card)
          .attempt
          .flatMap:
            case Left(OrderError(_)) =>
              (bgActions.get, retries.get).mapN:
                case (c, Some(g)) =>
                  expect.same(c, 1 -> 1.hour) |+|
                    expect.same(g.totalRetries, maxRetries)
                case _ =>
                  failure(s"Expected $maxRetries retries and reschedule")
            case _ => IO.pure(failure("Expected order error"))
    }

  test("failing to delete cart does not affect checkout"):
    forall(gen) { (uid, oid, pid, cart, card) =>
      Checkout[IO](
        failingCart(cart),
        successfulOrders(oid),
        successfulPayment(pid),
        retryPolicy
      ).process(uid, card)
        .map(expect.same(oid, _))
    }

  test("successful checkout"):
    forall(gen) { (uid, oid, pid, ct, card) =>
      Checkout[IO](
        successfulCart(ct),
        successfulOrders(oid),
        successfulPayment(pid),
        retryPolicy
      )
        .process(uid, card)
        .map(expect.same(oid, _))
    }

end CheckoutSuite

protected class TestOrders() extends Orders[IO]:
  def get(userId: UserId, orderId: OrderId): IO[Option[Order]] = ???
  def findBy(userId: UserId): IO[List[Order]]                  = ???
  def create(
      userId: UserId,
      paymentId: PaymentId,
      items: NonEmptyList[CartItem],
      total: Money
  ): IO[OrderId] = ???

protected class TestCart() extends ShoppingCart[IO]:
  def add(userId: UserId, itemId: ItemId, quantity: Quantity): IO[Unit] = ???
  def get(userId: UserId): IO[CartTotal]                                = ???
  def delete(userId: UserId): IO[Unit]                                  = ???
  def removeItem(userId: UserId, itemId: ItemId): IO[Unit]              = ???
  def update(userId: UserId, cart: Cart): IO[Unit]                      = ???
