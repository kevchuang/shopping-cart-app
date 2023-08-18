package com.kevchuang.shop.http.routes

import cats.Monad
import com.kevchuang.shop.domain.brand.BrandParam
import com.kevchuang.shop.services.Items
import io.circe.*
import io.circe.generic.auto.*
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityCodec.*
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import io.github.iltotore.iron.circe.given

final case class ItemsRoutes[F[_]: Monad](items: Items[F]) extends Http4sDsl[F]:
  private[routes] val prefixPath = "/items"

  private object BrandQueryParam
      extends OptionalQueryParamDecoderMatcher[BrandParam]("brand")

  private val httpRoutes: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root :? BrandQueryParam(brand) =>
      Ok(brand.fold(items.findAll)(b => items.findByBrand(b.toDomain)))
  }

  val routes: HttpRoutes[F] = Router(prefixPath -> httpRoutes)
