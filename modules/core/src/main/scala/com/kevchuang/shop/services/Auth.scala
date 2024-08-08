package com.kevchuang.shop.services

import cats.*
import cats.syntax.all.*
import com.kevchuang.shop.auth.{Crypto, Tokens}
import com.kevchuang.shop.config.types.TokenExpiration
import com.kevchuang.shop.domain.auth.*
import com.kevchuang.shop.http.auth.users.*
import dev.profunktor.auth.jwt.JwtToken
import dev.profunktor.redis4cats.RedisCommands
import io.circe.generic.auto.*
import io.circe.syntax.*
import io.github.iltotore.iron.circe.given

trait Auth[F[_]]:
  def createUser(userName: UserName, password: Password): F[JwtToken]
  def login(userName: UserName, password: Password): F[JwtToken]
  def logout(token: JwtToken, userName: UserName): F[Unit]
end Auth

object Auth:
  def make[F[_]: MonadThrow](
      crypto: Crypto,
      redis: RedisCommands[F, String, String],
      tokenExpiration: TokenExpiration,
      tokens: Tokens[F],
      users: Users[F]
  ): Auth[F] =
    new Auth[F]:
      def createUser(userName: UserName, password: Password): F[JwtToken] =
        users
          .find(userName)
          .flatMap:
            case Some(_) => UserNameInUse(userName).raiseError[F, JwtToken]
            case None =>
              for
                id    <- users.create(userName, crypto.encrypt(password))
                token <- tokens.create
                user   = User(id, userName).asJson.noSpaces
                _     <- redis.setEx(token.value, user, tokenExpiration.value)
                _ <- redis.setEx(
                       userName.value,
                       token.value,
                       tokenExpiration.value
                     )
              yield token

      def login(userName: UserName, password: Password): F[JwtToken] =
        users
          .find(userName)
          .flatMap:
            case Some(u) if u.password == crypto.encrypt(password) =>
              redis
                .get(userName.value)
                .flatMap:
                  case Some(t) => JwtToken(t).pure[F]
                  case None =>
                    for
                      token <- tokens.create
                      _ <- redis.setEx(
                             token.value,
                             u.asJson.noSpaces,
                             tokenExpiration.value
                           )
                      _ <- redis.setEx(
                             userName.value,
                             token.value,
                             tokenExpiration.value
                           )
                    yield token
            case Some(_) => InvalidPassword(userName).raiseError[F, JwtToken]
            case None    => UserNotFound(userName).raiseError[F, JwtToken]

      def logout(token: JwtToken, userName: UserName): F[Unit] =
        redis.del(token.value) *> redis.del(userName.value).void

end Auth
