package com.kevchuang.shop.http.codecs

import cats.effect.*
import com.kevchuang.shop.domain.auth.{*, given}
import dev.profunktor.auth.jwt.JwtToken
import io.circe.*
import io.circe.generic.auto.*
import io.github.iltotore.iron.*
import io.github.iltotore.iron.circe.given
import org.http4s.circe.*
import org.http4s.{EntityDecoder, EntityEncoder}

trait HttpEntities[F[_]: Concurrent]:
  given EntityEncoder[F, CreateUser] = jsonEncoderOf[F, CreateUser]
  given EntityDecoder[F, CreateUser] = accumulatingJsonOf[F, CreateUser]

  given EntityEncoder[F, JwtToken] = jsonEncoderOf[F, JwtToken]
end HttpEntities
