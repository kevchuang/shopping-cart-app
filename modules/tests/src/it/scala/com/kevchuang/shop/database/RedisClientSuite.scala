package com.kevchuang.shop.database

import cats.effect.*
import cats.effect.std.UUIDGen
import cats.implicits.*
import com.kevchuang.shop.auth.*
import com.kevchuang.shop.config.types.*
import com.kevchuang.shop.domain.auth.*
import com.kevchuang.shop.domain.brand.*
import com.kevchuang.shop.domain.cart.*
import com.kevchuang.shop.domain.id.*
import com.kevchuang.shop.domain.category.*
import com.kevchuang.shop.domain.item.*
import com.kevchuang.shop.http.auth.users.*
import com.kevchuang.shop.services.*
import com.kevchuang.shop.suite.ResourceSuite
import com.kevchuang.shop.utils.generators.*
import dev.profunktor.auth.jwt.*
import dev.profunktor.redis4cats.effect.Log
import dev.profunktor.redis4cats.effect.Log.NoOp
import dev.profunktor.redis4cats.{Redis, RedisCommands}
import io.github.iltotore.iron.*
import io.github.iltotore.iron.cats.given
import pdi.jwt.{JwtAlgorithm, JwtClaim}

import java.util.UUID
import scala.concurrent.duration.*

object RedisClientSuite extends ResourceSuite:
  given Log[IO] = NoOp.instance
  type Res = RedisCommands[IO, String, String]

  override def sharedResource: Resource[IO, Res] =
    Redis[IO]
      .utf8("redis://localhost")
      .beforeAll(_.flushAll)

  val thirtySeconds = 30.seconds
  val exp           = ShoppingCartExpiration(thirtySeconds)
  val jwtAuth       = JwtAuth.hmac("bar", JwtAlgorithm.HS256)
  val tokenConfig   = JwtAccessTokenKeyConfig("bar")
  val tokenExp      = TokenExpiration(thirtySeconds)
  val jwtClaim      = JwtClaim("test")
  val userJwtAuth   = UserJwtAuth(jwtAuth)

  test("Authentication") { redis =>
    val gen = for
      un1 <- userNameGen
      un2 <- userNameGen
      pw  <- passwordGen
    yield (un1, un2, pw)

    forall(gen) { case (un1, un2, pw) =>
      for
        t <- JwtExpire.make[IO].map(Tokens.make[IO](_, tokenConfig, tokenExp))
        c <- Crypto.make[IO](PasswordSalt("test"))
        a  = Auth.make(c, redis, tokenExp, t, new TestUsers(un2))
        u  = UsersAuth.common[IO](redis)
        x <- u.findUser(JwtToken("invalid"))(jwtClaim)
        y <- a.login(un1, pw).attempt // UserNotFound
        j <- a.createUser(un1, pw)
        e <- jwtDecode[IO](j, userJwtAuth.value).attempt
        k <- a.login(un2, pw).attempt // InvalidPassword
        w <- u.findUser(j)(jwtClaim)
        s <- redis.get(j.value)
        _ <- a.logout(j, un1)
        z <- redis.get(j.value)
      yield expect.all(
        x.isEmpty,
        y == Left(UserNotFound(un1)),
        e.isRight,
        k == Left(InvalidPassword(un2)),
        w.fold(false)(_.value.name === un1),
        s.nonEmpty,
        z.isEmpty
      )
    }
  }

  test("Shopping Cart") { redis =>
    val gen = for
      uid <- userIdGen
      it1 <- itemGen
      it2 <- itemGen
      q1  <- quantityGen
      q2  <- quantityGen
    yield (uid, it1, it2, q1, q2)

    forall(gen) { case (uid, it1, it2, q1, q2) =>
      Ref
        .of[IO, Map[ItemId, Item]](Map(it1.uuid -> it1, it2.uuid -> it2))
        .flatMap { ref =>
          val items = new TestItems(ref)
          val c     = ShoppingCart.make[IO](items, redis, exp)
          for
            x <- c.get(uid)
            _ <- c.add(uid, it1.uuid, q1)
            _ <- c.add(uid, it2.uuid, q1)
            y <- c.get(uid)
            _ <- c.removeItem(uid, it1.uuid)
            z <- c.get(uid)
            _ <- c.update(uid, Cart(Map(it2.uuid -> q2)))
            w <- c.get(uid)
            _ <- c.delete(uid)
            v <- c.get(uid)
          yield expect.all(
            x.items.isEmpty,
            y.items.size === 2,
            z.items.size === 1,
            v.items.isEmpty,
            w.items.headOption.fold(false)(_.quantity === q2)
          )
        }
    }
  }

end RedisClientSuite

protected class TestUsers(un: UserName) extends Users[IO]:
  def find(username: UserName): IO[Option[UserWithPassword]] = IO.pure {
    (username === un)
      .guard[Option]
      .as:
        val id = UUID.randomUUID
        UserWithPassword(UserId(id), un, EncryptedPassword("foo"))

  }
  def create(username: UserName, password: EncryptedPassword): IO[UserId] =
    UUIDGen.randomUUID[IO].map(UserId(_))

protected class TestItems(ref: Ref[IO, Map[ItemId, Item]]) extends Items[IO]:
  def findAll: IO[List[Item]] =
    ref.get.map(_.values.toList)
  def findByBrand(brand: BrandName): IO[List[Item]] =
    ref.get.map(_.values.filter(_.brand.name === brand).toList)
  def findById(itemId: ItemId): IO[Option[Item]] =
    ref.get.map(_.get(itemId))
  def create(item: CreateItem): IO[ItemId] =
    ID.make[IO, ItemId](ItemId(_)).flatTap { id =>
      val brand    = Brand(item.brandId, BrandName("Foo"))
      val category = Category(item.categoryId, CategoryName("foo"))
      val newItem =
        Item(id, item.name, item.description, item.price, brand, category)
      ref.update(_.updated(id, newItem))
    }
  def update(item: UpdateItem): IO[Unit] =
    ref.update(x =>
      x.get(item.id)
        .fold(x)(i => x.updated(item.id, i.copy(price = item.price)))
    )
