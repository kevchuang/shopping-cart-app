package com.kevchuang.shop.auth

import cats.effect.Sync
import cats.syntax.all.*
import com.kevchuang.shop.config.types.TokenExpiration
import com.kevchuang.shop.effects.JwtClock
import pdi.jwt.JwtClaim

trait JwtExpire[F[_]]:
  def expiresIn(claim: JwtClaim, exp: TokenExpiration): F[JwtClaim]
end JwtExpire

object JwtExpire:
  def make[F[_]: Sync]: F[JwtExpire[F]] =
    JwtClock[F].utc.map { implicit jClock =>
      new JwtExpire[F]:
        override def expiresIn(
            claim: JwtClaim,
            exp: TokenExpiration
        ): F[JwtClaim] =
          Sync[F].delay(claim.issuedNow.expiresIn(exp.value.toMillis))
    }
end JwtExpire
