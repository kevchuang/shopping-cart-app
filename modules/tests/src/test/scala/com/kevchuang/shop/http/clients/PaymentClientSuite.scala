package com.kevchuang.shop.http.clients

import cats.effect.IO
import com.kevchuang.shop.config.PaymentConfig
import com.kevchuang.shop.config.types.PaymentURI
import com.kevchuang.shop.domain.order.PaymentError
import com.kevchuang.shop.utils.generators.*
import io.github.iltotore.iron.*
import io.github.iltotore.iron.cats.given
import io.github.iltotore.iron.circe.given
import org.http4s.*
import org.http4s.Method.*
import org.http4s.Status.*
import org.http4s.circe.CirceEntityEncoder.*
import org.http4s.client.Client
import org.http4s.dsl.io.*
import org.http4s.implicits.*
import weaver.SimpleIOSuite
import weaver.scalacheck.Checkers

object PaymentClientSuite extends SimpleIOSuite with Checkers:

  val config = PaymentConfig(PaymentURI("http://localhost"))

  def routes(
      mkResponse: IO[Response[IO]]
  ): HttpApp[F] =
    HttpRoutes
      .of[IO] { case POST -> Root / "payments" =>
        mkResponse
      }
      .orNotFound

  val gen = for
    id      <- paymentIdGen
    payment <- paymentGen
  yield id -> payment

  test("Response 200 OK"):
    forall(gen) { (id, payment) =>
      val client = Client.fromHttpApp(routes(Ok(id)))

      PaymentClient
        .make[IO](config, client)
        .process(payment)
        .map(expect.same(id, _))
    }

  test("Response 409 Conflict"):
    forall(gen) { (id, payment) =>
      val client = Client.fromHttpApp(routes(Conflict(id)))

      PaymentClient
        .make[IO](config, client)
        .process(payment)
        .map(expect.same(id, _))
    }

  test("Response 500 Internal Server Error"):
    forall(paymentGen) { payment =>
      val client = Client.fromHttpApp(routes(InternalServerError()))

      PaymentClient
        .make[IO](config, client)
        .process(payment)
        .attempt
        .map:
          case Left(error) =>
            expect.same(error, PaymentError("Internal Server Error"))
          case Right(_) => failure("expected payment error")
    }
end PaymentClientSuite
