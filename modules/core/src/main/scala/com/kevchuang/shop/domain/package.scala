package com.kevchuang.shop

import cats.{Eq, Monoid, Show}
import io.circe.{Decoder, Encoder}
import squants.market.*

package object domain:
  given Eq[Currency] =
    Eq.and(Eq.and(Eq.by(_.code), Eq.by(_.symbol)), Eq.by(_.name))
  given Decoder[Money] = Decoder[BigDecimal].map(USD.apply)
  given Encoder[Money] = Encoder[BigDecimal].contramap(_.amount)
  given Eq[Money]      = Eq.and(Eq.by(_.amount), Eq.by(_.currency))
  given Monoid[Money] =
    new Monoid[Money]:
      def empty: Money                       = USD(0)
      def combine(x: Money, y: Money): Money = x + y
  given Show[Money] = Show.fromToString
end domain
