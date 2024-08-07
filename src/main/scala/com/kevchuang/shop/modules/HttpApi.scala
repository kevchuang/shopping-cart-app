package com.kevchuang.shop.modules

import cats.effect.Async
import cats.syntax.all.*
import com.kevchuang.shop.http.auth.users.CommonUser
import com.kevchuang.shop.http.routes.auth.{
  LoginRoutes,
  LogoutRoutes,
  UsersRoutes
}
import com.kevchuang.shop.http.routes.{
  BrandsRoutes,
  CategoriesRoutes,
  HealthRoutes,
  ItemsRoutes
}
import dev.profunktor.auth.JwtAuthMiddleware
import org.http4s.*
import org.http4s.server.{AuthMiddleware, Router}

abstract sealed class HttpApi[F[_]: Async] private (
    security: Security[F],
    services: Services[F]
):
  private val userMiddleware: AuthMiddleware[F, CommonUser] =
    JwtAuthMiddleware[F, CommonUser](
      security.userJwtAuth.value,
      security.userAuth.findUser
    )

  // auth routes
  private val login: LoginRoutes[F]   = LoginRoutes[F](security.auth)
  private val logout: LogoutRoutes[F] = LogoutRoutes[F](security.auth)
  private val users: UsersRoutes[F]   = UsersRoutes[F](security.auth)

  private val brands: BrandsRoutes[F] = BrandsRoutes[F](services.brands)
  private val categories: CategoriesRoutes[F] =
    CategoriesRoutes[F](services.categories)
  private val health: HealthRoutes[F] = HealthRoutes[F](services.healthCheck)
  private val items: ItemsRoutes[F]   = ItemsRoutes[F](services.items)

  private val routes: HttpRoutes[F] =
    brands.routes <+>
      categories.routes <+>
      health.routes <+>
      items.routes <+>
      login.routes <+>
      logout.routes(userMiddleware) <+>
      users.routes

  val httpApp: HttpApp[F] = Router[F]("/" -> routes).orNotFound
end HttpApi

object HttpApi:
  def make[F[_]: Async](
      security: Security[F],
      services: Services[F]
  ): HttpApi[F] =
    new HttpApi[F](security, services) {}
