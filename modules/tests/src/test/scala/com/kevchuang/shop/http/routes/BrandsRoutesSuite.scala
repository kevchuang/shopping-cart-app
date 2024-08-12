package com.kevchuang.shop.http.routes

import cats.effect.IO
import com.kevchuang.shop.domain.brand
import com.kevchuang.shop.domain.brand.{Brand, BrandId, BrandName}
import com.kevchuang.shop.services.Brands
import com.kevchuang.shop.suite.HttpSuite
import com.kevchuang.shop.utils.generators.brandGen
import io.github.iltotore.iron.*
import org.http4s.Method.*
import org.http4s.client.dsl.io.*
import org.http4s.Status as HttpStatus
import org.scalacheck.Gen
import io.circe.generic.auto.*
import io.github.iltotore.iron.circe.given
import org.http4s.syntax.literals.*

import java.util.UUID

object BrandsRouteSuite extends HttpSuite:

  def dataBrands(brands: List[Brand]): TestBrands =
    new TestBrands:
      override def findAll: IO[List[Brand]] = IO.pure(brands)
    end new

  def failingBrands(): TestBrands =
    new TestBrands:
      override def findAll: IO[List[Brand]] = IO.raiseError(DummyError)
    end new

  test("GET brands successful") {
    forall(Gen.listOf(brandGen)): b =>
      val request = GET(uri"/brands")
      val routes  = BrandsRoutes[IO](dataBrands(b)).routes

      expectHttpBodyAndStatus(routes, request)(b, HttpStatus.Ok)
  }

  test("GET brands failure") {
    val request = GET(uri"/brands")
    val routes  = BrandsRoutes[IO](failingBrands()).routes

    expectHttpFailure(routes, request)
  }
end BrandsRouteSuite

protected class TestBrands extends Brands[IO]:
  override def findAll: IO[List[brand.Brand]] = IO.pure(List.empty)

  override def create(name: BrandName): IO[BrandId] =
    IO.pure(BrandId(UUID.randomUUID()))
end TestBrands
