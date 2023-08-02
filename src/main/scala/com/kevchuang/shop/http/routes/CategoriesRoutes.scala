package com.kevchuang.shop.http.routes

import cats.Monad
import com.kevchuang.shop.services.Categories
import io.circe.generic.auto.*
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityEncoder.*
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router

final case class CategoriesRoutes[F[_]: Monad](
    categories: Categories[F]
) extends Http4sDsl[F]:
  private[routes] val prefixPath = "/categories"

  private val httpRoutes: HttpRoutes[F] = HttpRoutes.of[F] { case GET -> Root =>
    Ok(categories.findAll)
  }

  val routes: HttpRoutes[F] = Router(
    prefixPath -> httpRoutes
  )
