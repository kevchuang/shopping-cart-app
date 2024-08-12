package com.kevchuang.shop.http.routes

import cats.effect.IO
import com.kevchuang.shop.domain.category.*
import com.kevchuang.shop.services.Categories
import com.kevchuang.shop.suite.HttpSuite
import com.kevchuang.shop.utils.generators.categoryGen
import org.scalacheck.Gen
import io.circe.generic.auto.*
import io.github.iltotore.iron.*
import io.github.iltotore.iron.circe.given
import org.http4s.syntax.literals.*
import org.http4s.Status as HttpStatus
import org.http4s.Method.*
import org.http4s.client.dsl.io.*

import java.util.UUID

object CategoriesRouteSuite extends HttpSuite:
  def dataCategories(categories: List[Category]): TestCategories =
    new TestCategories:
      override def findAll: IO[List[Category]] = IO.pure(categories)
    end new

  def failingCategories(): TestCategories =
    new TestCategories:
      override def findAll: IO[List[Category]] = IO.raiseError(DummyError)
    end new

  test("GET categories successful") {
    forall(Gen.listOf(categoryGen)): categories =>
      val request = GET(uri"/categories")
      val routes  = CategoriesRoutes[IO](dataCategories(categories)).routes

      expectHttpBodyAndStatus(routes, request)(categories, HttpStatus.Ok)
  }

  test("GET categories failure") {
    val request = GET(uri"/categories")
    val routes  = CategoriesRoutes[IO](failingCategories()).routes

    expectHttpFailure(routes, request)
  }
end CategoriesRouteSuite

protected class TestCategories extends Categories[IO]:
  override def findAll: IO[List[Category]] = IO.pure(List.empty)
  override def create(name: CategoryName): IO[CategoryId] =
    IO.pure(CategoryId(UUID.randomUUID()))
end TestCategories
