package com.kevchuang.shop

import cats.effect.std.Supervisor
import cats.effect.{ExitCode, IO, IOApp}
import dev.profunktor.redis4cats.log4cats.*
import com.kevchuang.shop.config.AppConfig
import com.kevchuang.shop.http.server.HttpServer
import com.kevchuang.shop.modules.*
import com.kevchuang.shop.resources.AppResources
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

object Main extends IOApp:
  override def run(args: List[String]): IO[ExitCode] =
    given Logger[IO] = Slf4jLogger.getLogger[IO]

    for
      config <- AppConfig.load[IO]
      _      <- Logger[IO].info("Application configuration loaded")
      _ <- Supervisor[IO].use { implicit sp =>
             AppResources
               .make[IO](config)
               .evalMap: resources =>
                 val services = Services.make[IO](
                   resources.postgres,
                   resources.redis
                 )
                 val api = HttpApi.make[IO](services)
                 IO.pure(config.httpServerConfig -> api.httpApp)
               .flatMap:
                 case (serverConfig, serverApp) =>
                   HttpServer.start[IO](serverConfig, serverApp)
               .useForever
           }
    yield ExitCode.Success
end Main
