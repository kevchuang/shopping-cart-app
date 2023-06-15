package com.kevchuang

import cats.effect.{ExitCode, IO, IOApp}
import com.kevchuang.endpoint.Endpoint
import org.http4s.HttpRoutes
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import sttp.tapir.server.http4s.Http4sServerInterpreter

object Application extends IOApp:
  override def run(args: List[String]): IO[ExitCode] =
    val routes: HttpRoutes[IO] =
      Http4sServerInterpreter[IO]().toRoutes(List(Endpoint.helloServerEndpoint))

    EmberServerBuilder
      .default[IO]
      .withHttpApp(Router("/" -> routes).orNotFound)
      .build
      .use(server =>
        for
          _ <- IO.println(
            s"Go to http://localhost:${server.address.getPort}/docs to open SwaggerUI. Press ENTER key to exit."
          )
          _ <- IO.readLine
        yield ()
      )
      .as(ExitCode.Success)
