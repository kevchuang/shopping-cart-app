package com.kevchuang.endpoint

import cats.Monad
import cats.effect.kernel.Async
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import sttp.tapir.*
import sttp.tapir.server.http4s.Http4sServerInterpreter

class HelloWorldEndpoint[F[_]: Async] extends Http4sDsl[F]:
  private val sayHello: HttpRoutes[F] =
    Http4sServerInterpreter[F]().toRoutes(
      HelloWorldEndpoint.helloWorldEndpoint.serverLogicSuccess { _ =>
        Monad[F].pure("Hello World !")
      }
    )

  val routes: HttpRoutes[F] = sayHello

object HelloWorldEndpoint:
  private val helloWorldEndpoint: Endpoint[Unit, Unit, Unit, String, Any] =
    endpoint.get
      .in("hello")
      .out(stringBody)
