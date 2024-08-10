package com.kevchuang.shop.sql

import com.kevchuang.shop.domain.auth.*
import com.kevchuang.shop.domain.brand.*
import com.kevchuang.shop.domain.category.*
import com.kevchuang.shop.domain.item.*
import com.kevchuang.shop.domain.types.common.NotEmpty
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import skunk.*
import skunk.codec.all.*
import squants.market.*

import java.util.UUID

object codecs:

  val brandId: Codec[BrandId] = uuid.imap(BrandId(_))(_.value)
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

  val price: Codec[Money] = numeric.imap[Money](USD(_))(_.amount)

  val userId: Codec[UserId]     = uuid.imap(UserId(_))(_.value)
  val userName: Codec[UserName] = varchar.eimap(UserName.either(_))(_.value)
  val encryptedPassword: Codec[EncryptedPassword] =
    varchar.eimap(EncryptedPassword.either(_))(_.value)

end codecs
