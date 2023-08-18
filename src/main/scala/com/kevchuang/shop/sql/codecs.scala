package com.kevchuang.shop.sql

import com.kevchuang.shop.domain.brand.*
import com.kevchuang.shop.domain.category.*
import com.kevchuang.shop.domain.item.*
import com.kevchuang.shop.domain.price.*
import com.kevchuang.shop.domain.currency.*
import com.kevchuang.shop.domain.types.common.NotEmpty
import skunk.*
import skunk.codec.all.*
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import java.util.UUID

object codecs:

  val brandId: Codec[BrandId] =
    uuid.imap(BrandId(_))(_.value)
  val brandName: Codec[BrandName] =
    varchar.eimap(_.refineEither[Head[UpperCase]].map(BrandName(_)))(_.value)

  val categoryId: Codec[CategoryId] = uuid.imap(CategoryId(_))(_.value)
  val categoryName: Codec[CategoryName] =
    varchar.eimap(CategoryName.either(_))(_.value)

  val itemDescription: Codec[ItemDescription] =
    varchar.imap(ItemDescription(_))(_.value)
  val itemId: Codec[ItemId] = uuid.imap(ItemId(_))(_.value)
  val itemName: Codec[ItemName] =
    varchar.eimap(_.refineEither[NotEmpty].map(ItemName(_)))(_.value)

  val price: Codec[Price] =
    float8
      .eimap(a => a.refineEither[Positive].map(e => USD(Amount(e))))(
        _.amount.value
      )

end codecs
