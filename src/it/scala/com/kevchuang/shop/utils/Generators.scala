package com.kevchuang.shop.utils

import com.kevchuang.shop.domain.brand.*
import com.kevchuang.shop.domain.category.*
import com.kevchuang.shop.domain.currency.USD
import com.kevchuang.shop.domain.item.*
import com.kevchuang.shop.domain.price.{Amount, Price}
import org.scalacheck.Gen

import java.util.UUID

object Generators:

  val nonEmptyStringGen: Gen[String] =
    Gen
      .chooseNum(21, 40)
      .flatMap { n =>
        Gen.buildableOfN[String, Char](n, Gen.alphaChar)
      }

  def nesGen[A](f: String => A): Gen[A] =
    nonEmptyStringGen.map(f)

  def idGen[A](f: UUID => A): Gen[A] =
    Gen.uuid.map(f)

  val brandIdGen: Gen[BrandId] =
    idGen(BrandId.apply)

  val brandNameGen: Gen[BrandName] =
    nesGen(BrandName.apply)

  val categoryIdGen: Gen[CategoryId] =
    idGen(CategoryId.apply)

  val categoryNameGen: Gen[CategoryName] =
    nesGen(CategoryName.apply)

  val itemIdGen: Gen[ItemId] =
    idGen(ItemId.apply)

  val itemNameGen: Gen[ItemName] =
    nesGen(ItemName.apply)

  val itemDescriptionGen: Gen[ItemDescription] =
    nesGen(ItemDescription.apply)

  val priceGen: Gen[Price] =
    Gen.posNum[Long].map(n => USD(Amount(n.toDouble)))

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

end Generators
