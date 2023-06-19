package com.kevchuang.shop.http.server

import cats.effect.Async
import cats.effect.kernel.Resource
import com.kevchuang.shop.config.HttpServerConfig
import com.kevchuang.shop.http.routes.HelloWorldEndpoint
import fs2.io.net.Network
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.defaults.Banner
import org.http4s.server.{Router, Server}
import org.typelevel.log4cats.Logger

object HttpServer:

  private def showBanner[F[_]: Logger](server: Server): F[Unit] =
    Logger[F].info(s"\n${Banner.mkString("\n")}\nHTTP Server started at ${server.address}")

  def start[F[_]: Async: Logger: Network](config: HttpServerConfig): Resource[F, Server] =
    val helloWorldEndpoint = new HelloWorldEndpoint[F]
    val routes             = helloWorldEndpoint.routes
    val httpApp            = Router[F]("/" -> routes).orNotFound

    EmberServerBuilder
      .default[F]
      .withHost(config.host)
      .withPort(config.port)
      .withHttpApp(httpApp)
      .build
      .evalTap(showBanner[F])
