package com.kevchuang.shop.modules

import cats.effect.kernel.Async
import cats.syntax.all.*
import com.kevchuang.shop.http.routes.{
  BrandsRoutes,
  CategoriesRoutes,
  HealthRoutes,
  ItemsRoutes
}
import org.http4s.server.Router
import org.http4s.*

abstract sealed class HttpApi[F[_]: Async] private (
    services: Services[F]
):
  private val brands: BrandsRoutes[F] = BrandsRoutes[F](services.brands)
  private val categories: CategoriesRoutes[F] =
    CategoriesRoutes[F](services.categories)
  private val health: HealthRoutes[F] = HealthRoutes[F](services.healthCheck)
  private val items: ItemsRoutes[F]   = ItemsRoutes[F](services.items)
  private val routes: HttpRoutes[F] =
    brands.routes <+>
      categories.routes <+>
      health.routes <+>
      items.routes

  val httpApp: HttpApp[F] = Router[F]("/" -> routes).orNotFound
end HttpApi

object HttpApi:
  def make[F[_]: Async](services: Services[F]): HttpApi[F] =
    new HttpApi[F](services) {}
