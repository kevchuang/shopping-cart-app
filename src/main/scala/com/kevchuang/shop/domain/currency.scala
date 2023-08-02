package com.kevchuang.shop.domain

import cats.{Eq, Show}
import com.kevchuang.shop.domain.price.{Amount, Price}
import com.kevchuang.shop.macros.enumeration.*
import io.circe.{Decoder, Encoder}

object currency:
  enum Currency(val currency: String):
    case EUR extends Currency("EUR")
    case USD extends Currency("USD")
  end Currency

  given Decoder[Currency] = stringEnumDecoder[Currency]
  given Encoder[Currency] = stringEnumEncoder[Currency]
  given Eq[Currency]      = Eq.by(_.currency)
  given Show[Currency]    = Show.fromToString

  final case class EUR(amount: Amount) extends Price:
    val currency: Currency = Currency.EUR
  end EUR

  final case class USD(amount: Amount) extends Price:
    val currency: Currency = Currency.USD
  end USD
end currency
