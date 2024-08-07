package com.kevchuang.shop.auth

import cats.Monad
import cats.effect.std.UUIDGen
import cats.effect.*
import cats.syntax.all.*
import com.kevchuang.shop.config.types.*
import dev.profunktor.auth.jwt.{JwtSecretKey, JwtToken, jwtEncode}
import pdi.jwt.{JwtAlgorithm, JwtClaim}
import io.circe.syntax.*

trait Tokens[F[_]]:
  def create: F[JwtToken]
end Tokens

object Tokens:
  def make[F[_]: UUIDGen: Monad](
      jwtExpire: JwtExpire[F],
      tokenConfig: JwtAccessTokenKeyConfig,
      exp: TokenExpiration
  ): Tokens[F] =
    new Tokens[F]:
      def create: F[JwtToken] =
        for
          uuid     <- UUIDGen.randomUUID[F]
          claim    <- jwtExpire.expiresIn(JwtClaim(uuid.asJson.noSpaces), exp)
          secretKey = JwtSecretKey(tokenConfig.value)
          token    <- jwtEncode[F](claim, secretKey, JwtAlgorithm.HS256)
        yield token
end Tokens
