package com.kevchuang.shop

import cats.effect.{ExitCode, IO, IOApp}
import com.kevchuang.shop.config.AppConfig
import com.kevchuang.shop.http.server.HttpServer
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

object Main extends IOApp:
  override def run(args: List[String]): IO[ExitCode] =
    implicit val logger: Logger[IO] = Slf4jLogger.getLogger[IO]

    for
      config <- AppConfig.load[IO]
      _      <- Logger[IO].info("Application configuration loaded")
      _      <- HttpServer.start[IO](config.httpServerConfig).useForever
    yield ExitCode.Success
