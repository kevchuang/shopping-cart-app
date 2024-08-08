package com.kevchuang.shop.services

import cats.*
import cats.syntax.all.*
import com.kevchuang.shop.domain.auth.{*, given}
import com.kevchuang.shop.http.auth.users.*
import dev.profunktor.auth.jwt.JwtToken
import dev.profunktor.redis4cats.RedisCommands
import io.circe.generic.auto.*
import io.circe.parser.decode
import io.github.iltotore.iron.*
import io.github.iltotore.iron.circe.given
import pdi.jwt.JwtClaim

trait UsersAuth[F[_], A]:
  def findUser(token: JwtToken)(claim: JwtClaim): F[Option[A]]
end UsersAuth

object UsersAuth:
  def admin[F[_]: Applicative](
      adminToken: JwtToken,
      adminUser: AdminUser
  ): UsersAuth[F, AdminUser] =
    new UsersAuth[F, AdminUser]:
      def findUser(token: JwtToken)(claim: JwtClaim): F[Option[AdminUser]] =
        (token === adminToken)
          .guard[Option]
          .as(adminUser)
          .pure[F]

  def common[F[_]: Functor](
      redis: RedisCommands[F, String, String]
  ): UsersAuth[F, CommonUser] =
    new UsersAuth[F, CommonUser]:
      def findUser(token: JwtToken)(claim: JwtClaim): F[Option[CommonUser]] =
        redis
          .get(token.value)
          .map:
            _.flatMap: u =>
              decode[User](u).toOption.map(CommonUser(_))

end UsersAuth
