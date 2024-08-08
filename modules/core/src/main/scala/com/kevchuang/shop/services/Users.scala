package com.kevchuang.shop.services

import cats.effect.*
import cats.effect.std.UUIDGen
import cats.syntax.all.*
import com.kevchuang.shop.domain.auth.*
import com.kevchuang.shop.http.auth.users.*
import com.kevchuang.shop.sql.codecs.*
import skunk.*
import skunk.implicits.*
import io.github.iltotore.iron.*

trait Users[F[_]]:
  def find(userName: UserName): F[Option[UserWithPassword]]
  def create(userName: UserName, password: EncryptedPassword): F[UserId]
end Users

object Users:
  def make[F[_]: UUIDGen: MonadCancelThrow](
      postgres: Resource[F, Session[F]]
  ): Users[F] =
    new Users[F]:
      import UsersSQL.*

      def find(userName: UserName): F[Option[UserWithPassword]] =
        postgres.use: session =>
          session
            .prepareR(selectUser)
            .use: query =>
              query
                .option(userName)
                .map:
                  case Some(user ~ password) =>
                    UserWithPassword(user.id, user.name, password).some
                  case _ =>
                    none[UserWithPassword]

      def create(userName: UserName, password: EncryptedPassword): F[UserId] =
        postgres.use: session =>
          session
            .prepareR(insertUser)
            .use: command =>
              UUIDGen
                .randomUUID[F]
                .map(UserId(_))
                .flatMap: id =>
                  command
                    .execute(User(id, userName), password)
                    .as(id)
                    .recoverWith:
                      case SqlState.UniqueViolation(_) =>
                        UserNameInUse(userName).raiseError[F, UserId]
end Users

private object UsersSQL:
  private val userCodec: Codec[User ~ EncryptedPassword] =
    (userId ~ userName ~ encryptedPassword).imap { case id ~ name ~ password =>
      (User(id, name), password)
    } { case user ~ password => ((user.id, user.name), password) }

  val selectUser: Query[UserName, User ~ EncryptedPassword] =
    sql"""SELECT * FROM users WHERE name = $userName""".query(userCodec)

  val insertUser: Command[User ~ EncryptedPassword] =
    sql"""INSERT INTO users VALUES ($userCodec)""".command
end UsersSQL
