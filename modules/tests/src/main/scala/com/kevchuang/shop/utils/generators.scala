package com.kevchuang.shop.utils

import com.kevchuang.shop.domain.auth.*
import com.kevchuang.shop.domain.brand.*
import com.kevchuang.shop.domain.category.*
import com.kevchuang.shop.domain.currency.USD
import com.kevchuang.shop.domain.item.*
import com.kevchuang.shop.domain.price.{Amount, Price}
import com.kevchuang.shop.domain.types.common.NotEmpty
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import org.scalacheck.Gen

import java.util.UUID

object generators:

  val nonEmptyStringGen: Gen[String :| (NotEmpty & Head[UpperCase])] =
    Gen
      .chooseNum(21, 40)
      .flatMap { n =>
        Gen
          .buildableOfN[String, Char](n, Gen.alphaChar)
          .map(_.capitalize.refine[NotEmpty & Head[UpperCase]])
      }

  def nesGen[A](f: String :| (NotEmpty & Head[UpperCase]) => A): Gen[A] =
    nonEmptyStringGen.map(f)

  def idGen[A](f: UUID => A): Gen[A] =
    Gen.uuid.map(f)

  val brandIdGen: Gen[BrandId] =
    idGen(BrandId(_))

  val brandNameGen: Gen[BrandName] =
    nesGen(BrandName(_))

  val categoryIdGen: Gen[CategoryId] =
    idGen(CategoryId(_))

  val categoryNameGen: Gen[CategoryName] =
    nesGen(CategoryName(_))

  val itemIdGen: Gen[ItemId] =
    idGen(ItemId(_))

  val itemNameGen: Gen[ItemName] =
    nesGen(ItemName(_))

  val itemDescriptionGen: Gen[ItemDescription] =
    nesGen(ItemDescription(_))

  val priceGen: Gen[Price] =
    Gen.posNum[Long].map(n => USD(Amount(n.toDouble.refine[Positive])))

  val userNameGen: Gen[UserName] =
    nesGen(UserName(_))

  val passwordGen: Gen[Password] =
    nesGen(Password(_))

  val encryptedPasswordGen: Gen[EncryptedPassword] =
    nesGen(EncryptedPassword(_))

  val brandGen: Gen[Brand] =
    for
      i <- brandIdGen
      n <- brandNameGen
    yield Brand(i, n)

  val categoryGen: Gen[Category] =
    for
      i <- categoryIdGen
      n <- categoryNameGen
    yield Category(i, n)

  val itemGen: Gen[Item] =
    for
      id          <- itemIdGen
      name        <- itemNameGen
      description <- itemDescriptionGen
      price       <- priceGen
      brand       <- brandGen
      category    <- categoryGen
    yield Item(id, name, description, price, brand, category)

end generators
