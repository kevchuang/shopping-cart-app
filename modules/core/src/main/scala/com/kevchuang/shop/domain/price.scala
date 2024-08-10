package com.kevchuang.shop.domain

import cats.{Eq, Show}
import com.kevchuang.shop.domain.currency.*
import io.circe.*
import io.github.iltotore.iron.*
import io.github.iltotore.iron.cats.given
import io.github.iltotore.iron.constraint.all.*

object price:
  opaque type Amount = BigDecimal :| Positive
  object Amount extends RefinedTypeOps[BigDecimal, Positive, Amount]

  abstract class Price:
    def amount: Amount
    def currency: Currency
  end Price

  given Decoder[Price] =
    Decoder[BigDecimal].emap(_.refineEither[Positive].map(USD.apply))
  given Encoder[Price] = Encoder[BigDecimal].contramap(_.amount)

  given Eq[Price]   = Eq.and(Eq.by(_.amount), Eq.by(_.currency))
  given Show[Price] = Show.fromToString
end price
