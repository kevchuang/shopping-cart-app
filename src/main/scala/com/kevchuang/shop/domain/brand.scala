package com.kevchuang.shop.domain

import java.util.UUID

object brand:
  opaque type BrandId = UUID
  object BrandId:
    def apply(uuid: UUID): BrandId = uuid

  opaque type BrandName = String
  object BrandName:
    def apply(s: String): BrandName = s.toLowerCase.capitalize

  final case class Brand(uuid: BrandId, name: BrandName)
