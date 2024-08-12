package com.kevchuang.shop.http

import cats.syntax.all.*
import com.kevchuang.shop.domain.item.ItemId
import io.github.iltotore.iron.*

import java.util.UUID

object vars:
  protected class UUIDVar[A](f: UUID => A):
    def unapply(str: String): Option[A] =
      Either.catchNonFatal(f(UUID.fromString(str))).toOption

  object ItemIdVar extends UUIDVar(ItemId(_))
end vars
