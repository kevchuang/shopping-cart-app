package com.kevchuang.shop.database

import cats.effect.*
import cats.effect.std.UUIDGen
import cats.implicits.*
import com.kevchuang.shop.auth.*
import com.kevchuang.shop.config.types.*
import com.kevchuang.shop.domain.auth.*
import com.kevchuang.shop.http.auth.users.*
import com.kevchuang.shop.services.{Auth, Users, UsersAuth}
import com.kevchuang.shop.suite.ResourceSuite
import com.kevchuang.shop.utils.Generators.*
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
