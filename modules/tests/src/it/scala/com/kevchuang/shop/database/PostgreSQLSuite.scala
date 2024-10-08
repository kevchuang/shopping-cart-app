package com.kevchuang.shop.database

import cats.data.NonEmptyList
import cats.effect.*
import cats.implicits.*
import com.kevchuang.shop.domain.brand.BrandId
import com.kevchuang.shop.domain.category.CategoryId
import com.kevchuang.shop.domain.given
import com.kevchuang.shop.domain.item.CreateItem
import com.kevchuang.shop.domain.order.Order
import com.kevchuang.shop.services.*
import com.kevchuang.shop.suite.ResourceSuite
import com.kevchuang.shop.utils.generators.*
import io.github.iltotore.iron.cats.given
import natchez.Trace.Implicits.noop
import org.scalacheck.Gen
import skunk.*
import skunk.implicits.*

object PostgreSQLSuite extends ResourceSuite:
  type Res = Resource[IO, Session[IO]]

  val flushTables: List[Command[Void]] = List("brands", "items", "users").map:
    table => sql"DELETE FROM #$table".command

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

  test("Items") { postgres =>
    forall(itemGen) { item =>
      def newItem(
          bid: Option[BrandId],
          cid: Option[CategoryId]
      ) = CreateItem(
        name = item.name,
        description = item.description,
        price = item.price,
        brandId = bid.getOrElse(item.brand.uuid),
        categoryId = cid.getOrElse(item.category.uuid)
      )

      val b = Brands.make[IO](postgres)
      val c = Categories.make[IO](postgres)
      val i = Items.make[IO](postgres)

      for
        x        <- i.findAll
        _        <- b.create(item.brand.name)
        d        <- b.findAll.map(_.headOption.map(_.uuid))
        _        <- c.create(item.category.name)
        e        <- c.findAll.map(_.headOption.map(_.uuid))
        _        <- i.create(newItem(d, e))
        y        <- i.findAll
        headItem <- IO.fromOption(y.headOption)(new Exception(""))
        itemById <- i.findById(headItem.uuid)
      yield expect.all(
        x.isEmpty,
        y.count(_.name === item.name) === 1,
        itemById === Some(headItem)
      )
    }
  }

  test("Orders") { postgres =>
    val gen = for
      randomOid <- orderIdGen
      un        <- userNameGen
      pw        <- encryptedPasswordGen
      pid       <- paymentIdGen
      items     <- Gen.nonEmptyListOf(cartItemGen).map(NonEmptyList.fromListUnsafe)
      total     <- priceGen
    yield (randomOid, un, pw, pid, items, total)

    forall(gen) { (randomOid, un, pw, pid, items, total) =>
      val o = Orders.make[IO](postgres)
      val u = Users.make[IO](postgres)

      for
        uid        <- u.create(un, pw)
        emptyOrder <- o.get(uid, randomOid)
        oid        <- o.create(uid, pid, items, total)
        order      <- o.get(uid, oid)
        expectedOrder =
          Order(
            oid,
            pid,
            items.map(i => (i.item.uuid, i.quantity)).toList.toMap,
            total
          )
        find <- o.findBy(uid)
      yield expect.all(
        emptyOrder.isEmpty,
        order.contains(expectedOrder),
        find.nonEmpty
      )
    }
  }

  test("Users") { postgres =>
    val gen = for
      u <- userNameGen
      p <- encryptedPasswordGen
    yield u -> p

    forall(gen) { case (userName, password) =>
      val u = Users.make[IO](postgres)
      for
        d <- u.create(userName, password)
        x <- u.find(userName)
        z <- u.create(userName, password).attempt
      yield expect.all(x.count(_.id === d) === 1, z.isLeft)
    }
  }
