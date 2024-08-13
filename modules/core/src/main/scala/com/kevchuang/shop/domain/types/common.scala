package com.kevchuang.shop.domain.types

import io.github.iltotore.iron.*
import io.github.iltotore.iron.compileTime.{NumConstant, longValue, stringValue}
import io.github.iltotore.iron.constraint.all.*

import scala.compiletime.constValue
import scala.util.control.NoStackTrace

object common:

  type NotEmpty     = Not[Empty]
  type ValidNumeric = ForAll[Digit]

  final case class RefineError(message: String) extends NoStackTrace

  final class Size[V]

  trait SizeConstraint[A, V] extends Constraint[A, Size[V]]:
    override inline def message: String =
      s"Must contain ${stringValue[V]} numbers"
  end SizeConstraint

  given [V]: SizeConstraint[Int, V] with
    override inline def test(value: Int): Boolean =
      value.toString.length == constValue[V]

  given [V]: SizeConstraint[Long, V] with
    override inline def test(value: Long): Boolean =
      value.toString.length == constValue[V]

end common
