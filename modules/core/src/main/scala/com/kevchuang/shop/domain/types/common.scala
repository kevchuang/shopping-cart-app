package com.kevchuang.shop.domain.types

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import scala.util.control.NoStackTrace

object common:

  type NotEmpty = Not[Empty]

  final case class RefineError(message: String) extends NoStackTrace

end common
