package com.kevchuang.server

import cats.effect.Async
import cats.effect.kernel.Resource
import com.kevchuang.endpoint.HelloWorldEndpoint
import fs2.io.net.Network
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.{Router, Server}

object HttpServer:

  def start[F[_]: Async: Network]: Resource[F, Server] =
    val helloWorldEndpoint = new HelloWorldEndpoint[F]
    val routes = helloWorldEndpoint.routes
    val httpApp = Router[F]("/" -> routes).orNotFound

    EmberServerBuilder
      .default[F]
      .withHttpApp(httpApp)
      .build
