package com.kevchuang.shop.domain

import cats.derived.*
import cats.{Eq, Show}
import com.kevchuang.shop.domain.types.common.NotEmpty
import io.github.iltotore.iron.*
import io.github.iltotore.iron.cats.given
import io.github.iltotore.iron.constraint.all.*

import java.util.UUID

object category:
  opaque type CategoryId = UUID :| Pure
  object CategoryId extends RefinedTypeOpsImpl[UUID, Pure, CategoryId]

  opaque type CategoryName = String :| NotEmpty
  object CategoryName extends RefinedTypeOpsImpl[String, NotEmpty, CategoryName]

  final case class Category(uuid: CategoryId, name: CategoryName)
      derives Eq,
        Show
end category
