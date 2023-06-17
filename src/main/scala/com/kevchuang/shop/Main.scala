package com.kevchuang.shop

import cats.effect.{ExitCode, IO, IOApp}
import com.kevchuang.shop.http.server.HttpServer

object Main extends IOApp:
  override def run(args: List[String]): IO[ExitCode] =
    HttpServer
      .start[IO]
      .use(server => IO.println(s"Server started on port ${server.address.getPort}") *> IO.never.as(ExitCode.Success))
