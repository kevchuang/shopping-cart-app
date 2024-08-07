package com.kevchuang.shop.domain

import cats.{Eq, Show}
import cats.derived.*
import com.kevchuang.shop.domain.price.{Amount, Price}
import com.kevchuang.shop.macros.enumeration.*
import io.circe.{Decoder, Encoder}
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import io.github.iltotore.iron.cats.given

object currency:
  enum Currency(
      val currency: String :| (ForAll[UpperCase] & MinLength[3] & MaxLength[3])
  ):
    case EUR extends Currency("EUR")
    case USD extends Currency("USD")
  end Currency

  given Decoder[Currency] = stringEnumDecoder[Currency]
  given Encoder[Currency] = stringEnumEncoder[Currency]

  given Eq[Currency] = Eq.by(_.currency)

  final case class EUR(amount: Amount) extends Price derives Eq, Show:
    val currency: Currency = Currency.EUR
  end EUR

  final case class USD(amount: Amount) extends Price derives Eq, Show:
    val currency: Currency = Currency.USD
  end USD
end currency
