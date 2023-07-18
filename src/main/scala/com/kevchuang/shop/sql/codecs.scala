package com.kevchuang.shop.sql

import com.kevchuang.shop.domain.brand.{BrandId, BrandName}
import skunk.*
import skunk.codec.all.*

object codecs:
  val brandId: Codec[BrandId]     = uuid.imap(BrandId(_))(_.value)
  val brandName: Codec[BrandName] = varchar.imap(BrandName(_))(_.value)
