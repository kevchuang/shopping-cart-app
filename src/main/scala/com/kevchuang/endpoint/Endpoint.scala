package com.kevchuang.endpoint

import cats.effect.IO
import sttp.tapir.*
import sttp.tapir.server.ServerEndpoint

object Endpoint:
  private val helloEndpoint: PublicEndpoint[Unit, Unit, String, Any] = endpoint.get
    .out(stringBody)

  val helloServerEndpoint: ServerEndpoint[Any, IO] = helloEndpoint.serverLogicSuccess(_ => IO.pure("Hello World"))
