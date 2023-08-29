package com.kevchuang.shop.http.routes

import cats.effect.kernel.Async
import com.kevchuang.shop.domain.brand.Brand
import com.kevchuang.shop.services.Brands
import io.circe.generic.auto.*
import io.github.iltotore.iron.circe.given
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityEncoder.*
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router

final case class BrandsRoutes[F[_]: Async](
    brands: Brands[F]
) extends Http4sDsl[F]:
  private[routes] val prefixPath = "/brands"

  private val httpRoutes: HttpRoutes[F] =
    HttpRoutes.of[F]:
      case GET -> Root => Ok(brands.findAll)

  val routes: HttpRoutes[F] = Router(
    prefixPath -> httpRoutes
  )
end BrandsRoutes
