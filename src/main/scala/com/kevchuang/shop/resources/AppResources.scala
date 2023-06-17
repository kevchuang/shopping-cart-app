package com.kevchuang.shop.resources

import cats.effect.Resource
import com.kevchuang.shop.config.AppConfig
import skunk.Session

sealed abstract class AppResources[F[_]] private (
    val postgres: Resource[F, Session[F]]
)

object AppResources:
  def make[F[_]](
      appConfig: AppConfig
  ): Resource[F, AppResources[F]] = ???
    
