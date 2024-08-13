package com.kevchuang.shop.domain

import com.kevchuang.shop.domain.types.common.{Size, ValidNumeric, given}
import io.github.iltotore.iron.*
import io.github.iltotore.iron.compileTime.{NumConstant, stringValue}
import io.github.iltotore.iron.constraint.all.*

object checkout:
  private type Rgx = "^[a-zA-Z]+(([',. -][a-zA-Z ])?[a-zA-Z]*)*$"

  private type CardNameConstraint = Match[Rgx]
  opaque type CardName            = String :| CardNameConstraint
  object CardName extends RefinedTypeOps[String, CardNameConstraint, CardName]

  opaque type CardNumber = Long :| Size[16]
  object CardNumber extends RefinedTypeOps[Long, Size[16], CardNumber]

  private type CardExpirationConstraint = ValidNumeric & FixedLength[4]
  opaque type CardExpiration            = String :| CardExpirationConstraint
  object CardExpiration
      extends RefinedTypeOps[String, CardExpirationConstraint, CardExpiration]

  opaque type CardCVV = Int :| Size[3]
  object CardCVV extends RefinedTypeOps[Int, Size[3], CardCVV]

  final case class Card(
      name: CardName,
      number: CardNumber,
      expiration: CardExpiration,
      cvv: CardCVV
  )
end checkout
