package com.kevchuang.shop.domain

import cats.{Eq, Show}
import com.kevchuang.shop.domain.currency.{Currency, USD}
import io.circe.*

object price:
  opaque type Amount = Double
  object Amount:
    def apply(amount: Double): Amount = amount

    extension (amount: Amount) def value: Double = amount

    given Decoder[Amount] = Decoder.decodeDouble.map(Amount.apply)
    given Encoder[Amount] = Encoder.encodeDouble.contramap(_.value)
  end Amount

  abstract class Price:
    def amount: Amount
    def currency: Currency
  end Price

  given Decoder[Price] = Decoder[Double].map(USD.apply)
  given Encoder[Price] = Encoder[Double].contramap(_.amount)

  given Eq[Price]   = Eq.and(Eq.by(_.amount), Eq.by(_.currency))
  given Show[Price] = Show.fromToString
end price
