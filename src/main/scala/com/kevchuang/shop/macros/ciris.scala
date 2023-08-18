package com.kevchuang.shop.macros

import _root_.ciris.ConfigDecoder
import io.github.iltotore.iron.{:|, Constraint, refineOption}
import cats.Show

object ciris:

  inline given [T, A, B](using
      inline decoder: ConfigDecoder[T, A],
      inline constraint: Constraint[A, B],
      inline show: Show[A]
  ): ConfigDecoder[T, A :| B] =
    decoder.mapOption("")(_.refineOption)
