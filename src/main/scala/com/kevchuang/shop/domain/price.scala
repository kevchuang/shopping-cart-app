package com.kevchuang.shop.domain

import cats.{Eq, Show}
import com.kevchuang.shop.domain.currency.*
import io.circe.*
import io.github.iltotore.iron.*
import io.github.iltotore.iron.cats.given
import io.github.iltotore.iron.constraint.all.*

object price:
  opaque type Amount = Double :| Positive
  object Amount extends RefinedTypeOpsImpl[Double, Positive, Amount]

  abstract class Price:
    def amount: Amount
    def currency: Currency
  end Price

  given Decoder[Price] =
    Decoder[Double].emap(_.refineEither[Positive].map(USD.apply))
  given Encoder[Price] = Encoder[Double].contramap(_.amount)

  given Eq[Price]   = Eq.and(Eq.by(_.amount), Eq.by(_.currency))
  given Show[Price] = Show.fromToString
end price
