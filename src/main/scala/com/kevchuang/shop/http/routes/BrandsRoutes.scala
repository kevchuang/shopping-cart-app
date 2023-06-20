package com.kevchuang.shop.http.routes

import cats.Monad
import cats.effect.kernel.Async
import com.kevchuang.shop.services.Brands
import org.http4s.HttpRoutes
import sttp.tapir.Endpoint
import sttp.tapir.*
import sttp.tapir.server.http4s.Http4sServerInterpreter

final case class BrandsRoutes[F[_]: Async](
    brands: Brands[F]
):
  private val getBrandsEndpoint: Endpoint[Unit, Unit, Unit, String, Any] =
    endpoint.get
      .in("brands")
      .out(stringBody)

  private val getBrands: HttpRoutes[F]                                   =
    Http4sServerInterpreter[F]().toRoutes(
      getBrandsEndpoint.serverLogicSuccess { _ =>
        Monad[F].pure("Hello World !")
      }
    )

  val routes: HttpRoutes[F] = getBrands
