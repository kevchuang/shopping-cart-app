package com.kevchuang.shop.utils

import com.kevchuang.shop.domain.brand.*
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

  val brandGen: Gen[Brand] =
    for
      i <- brandIdGen
      n <- brandNameGen
    yield Brand(i, n)
