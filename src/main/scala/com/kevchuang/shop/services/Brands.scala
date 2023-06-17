package com.kevchuang.shop.services

import cats.effect.kernel.Resource
import com.kevchuang.shop.domain.brand.*
import skunk.Session

trait Brands[F[_]]:
  def findAll: F[List[Brand]]
  def create(name: BrandName): F[BrandId]

object Brands:
  def make[F[_]](postgres: Resource[F, Session[F]]): Brands[F] =
    new Brands[F]:
      def findAll: F[List[Brand]] = ???

      def create(name: BrandName): F[BrandId] = ???
