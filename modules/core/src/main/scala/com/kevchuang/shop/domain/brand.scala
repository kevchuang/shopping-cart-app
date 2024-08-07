package com.kevchuang.shop.domain

import cats.derived.*
import cats.{Eq, Show}
import com.kevchuang.shop.domain.types.common.*
import io.github.iltotore.iron.*
import io.github.iltotore.iron.cats.given
import io.github.iltotore.iron.constraint.all.*
import org.http4s.{ParseFailure, QueryParamDecoder}

import java.util.UUID

object brand:
  opaque type BrandId = UUID :| Pure
  object BrandId extends RefinedTypeOps[UUID, Pure, BrandId]

  opaque type BrandName = String :| Head[UpperCase]
  object BrandName extends RefinedTypeOps[String, Head[UpperCase], BrandName]

  opaque type BrandParam = String :| NotEmpty
  object BrandParam extends RefinedTypeOps[String, NotEmpty, BrandParam]:
    extension (brandParam: BrandParam)
      def toDomain: BrandName =
        BrandName(
          brandParam.toLowerCase.capitalize.refine[Head[UpperCase]]
        )

    given QueryParamDecoder[BrandParam] =
      QueryParamDecoder[String].emap(s =>
        s.refineEither[NotEmpty]
          .fold(
            e => Left(ParseFailure("BrandParam decoder failure", e)),
            value => Right(BrandParam(value))
          )
      )
  end BrandParam

  final case class Brand(uuid: BrandId, name: BrandName) derives Eq, Show
end brand
