package com.kevchuang.shop.modules

import cats.effect.kernel.Async
import com.kevchuang.shop.http.routes.BrandsRoutes
import org.http4s.server.Router
import org.http4s.{HttpApp, HttpRoutes}

abstract sealed class HttpApi[F[_]: Async] private (
    services: Services[F]
):
  private val brands: BrandsRoutes[F] = BrandsRoutes[F](services.brands)
  private val routes: HttpRoutes[F]   = brands.routes

  val httpApp: HttpApp[F] = Router[F]("/" -> routes).orNotFound

object HttpApi:
  def make[F[_]: Async](services: Services[F]): HttpApi[F] =
    new HttpApi[F](services) {}
