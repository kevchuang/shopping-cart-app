package com.kevchuang.shop.sql

import com.kevchuang.shop.domain.brand.*
import com.kevchuang.shop.domain.category.*
import com.kevchuang.shop.domain.item.*
import com.kevchuang.shop.domain.price.*
import com.kevchuang.shop.domain.currency.*
import skunk.*
import skunk.codec.all.*

object codecs:

  val brandId: Codec[BrandId]     = uuid.imap(BrandId(_))(_.value)
  val brandName: Codec[BrandName] = varchar.imap(BrandName(_))(_.value)

  val categoryId: Codec[CategoryId]     = uuid.imap(CategoryId(_))(_.value)
  val categoryName: Codec[CategoryName] = varchar.imap(CategoryName(_))(_.value)

  val itemDescription: Codec[ItemDescription] =
    varchar.imap(ItemDescription(_))(_.value)
  val itemId: Codec[ItemId]     = uuid.imap(ItemId(_))(_.value)
  val itemName: Codec[ItemName] = varchar.imap(ItemName(_))(_.value)

  val price: Codec[Price] = float8.imap(a => USD(Amount(a)))(_.amount.value)

end codecs
