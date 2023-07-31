package com.kevchuang.shop.database

import cats.effect.*
import cats.implicits.*
import com.kevchuang.shop.services.Brands
import com.kevchuang.shop.suite.ResourceSuite
import com.kevchuang.shop.utils.Generators.brandGen
import natchez.Trace.Implicits.noop
import skunk.*
import skunk.implicits.*

object PostgreSQLSuite extends ResourceSuite:
  type Res = Resource[IO, Session[IO]]

  val flushTables: List[Command[Void]] = List("brands").map { table =>
    sql"DELETE FROM #$table".command
  }

  override def sharedResource: Resource[IO, Resource[IO, Session[IO]]] =
    Session
      .pooled[IO](
        host = "localhost",
        port = 5432,
        user = "postgres",
        password = Some("my-password"),
        database = "store",
        max = 10
      )
      .beforeAll(_.use(s => flushTables.traverse_(s.execute)))

  test("Brands") { postgres =>
    forall(brandGen) { brand =>
      val b = Brands.make[IO](postgres)
      for
        x <- b.findAll
        _ <- b.create(brand.name)
        y <- b.findAll
        z <- b.create(brand.name).attempt
      yield expect.all(
        x.isEmpty,
        y.count(_.name == brand.name) === 1,
        z.isLeft
      )
    }
  }
