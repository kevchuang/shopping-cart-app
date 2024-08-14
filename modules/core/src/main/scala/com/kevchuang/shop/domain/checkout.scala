package com.kevchuang.shop.domain

import cats.derived.*
import cats.{Eq, Show}
import com.kevchuang.shop.domain.types.common.{Size, ValidNumeric, given}
import io.github.iltotore.iron.*
import io.github.iltotore.iron.cats.given
import io.github.iltotore.iron.constraint.all.*

object checkout:
  private type Rgx = "^[a-zA-Z]+(([',. -][a-zA-Z ])?[a-zA-Z]*)*$"

  type CardNameConstraint = Match[Rgx]
  opaque type CardName    = String :| CardNameConstraint
  object CardName extends RefinedTypeOps[String, CardNameConstraint, CardName]

  opaque type CardNumber = Long :| Size[16]
  object CardNumber extends RefinedTypeOps[Long, Size[16], CardNumber]

  type CardExpirationConstraint = ValidNumeric & FixedLength[4]
  opaque type CardExpiration    = String :| CardExpirationConstraint
  object CardExpiration
      extends RefinedTypeOps[String, CardExpirationConstraint, CardExpiration]

  opaque type CardCVV = Int :| Size[3]
  object CardCVV extends RefinedTypeOps[Int, Size[3], CardCVV]

  final case class Card(
      name: CardName,
      number: CardNumber,
      expiration: CardExpiration,
      cvv: CardCVV
  ) derives Eq,
        Show
end checkout
