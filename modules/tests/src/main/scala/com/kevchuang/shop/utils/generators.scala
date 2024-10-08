package com.kevchuang.shop.utils

import com.kevchuang.shop.domain.auth.*
import com.kevchuang.shop.domain.brand.*
import com.kevchuang.shop.domain.cart.*
import com.kevchuang.shop.domain.category.*
import com.kevchuang.shop.domain.checkout.*
import com.kevchuang.shop.domain.item.*
import com.kevchuang.shop.domain.order.*
import com.kevchuang.shop.domain.payment.*
import com.kevchuang.shop.domain.types.common.{NotEmpty, Size}
import com.kevchuang.shop.http.auth.users.*
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import org.scalacheck.Gen
import squants.market.{Money, USD}

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
    nesGen(s => BrandName(s.assume[Alphanumeric]))

  val categoryIdGen: Gen[CategoryId] =
    idGen(CategoryId(_))

  val categoryNameGen: Gen[CategoryName] =
    nesGen(CategoryName(_))

  val orderIdGen: Gen[OrderId] =
    idGen(OrderId(_))

  val itemIdGen: Gen[ItemId] =
    idGen(ItemId(_))

  val itemNameGen: Gen[ItemName] =
    nesGen(ItemName(_))

  val itemDescriptionGen: Gen[ItemDescription] =
    nesGen(ItemDescription(_))

  val paymentIdGen: Gen[PaymentId] =
    idGen(PaymentId(_))

  val priceGen: Gen[Money] =
    Gen.posNum[BigDecimal].map(USD(_))

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

  val userIdGen: Gen[UserId] =
    idGen(UserId(_))

  val userGen: Gen[User] =
    for
      i <- userIdGen
      n <- userNameGen
    yield User(i, n)

  val commonUserGen: Gen[CommonUser] =
    userGen.map(CommonUser(_))

  val quantityGen: Gen[Quantity] =
    Gen.posNum[Int].map(n => Quantity(n.assume[Positive]))

  val itemMapGen: Gen[(ItemId, Quantity)] =
    for
      i <- itemIdGen
      q <- quantityGen
    yield i -> q

  val cartGen: Gen[Cart] =
    Gen.nonEmptyMap(itemMapGen).map(Cart.apply)

  val cartItemGen: Gen[CartItem] =
    for
      i <- itemGen
      q <- quantityGen
    yield CartItem(i, q)

  val cartTotalGen: Gen[CartTotal] =
    for
      c <- Gen.nonEmptyListOf(cartItemGen)
      t <- priceGen
    yield CartTotal(c, t)

  val cardNameGen: Gen[CardName] =
    Gen
      .stringOf(Gen.oneOf(('a' to 'z') ++ ('A' to 'Z')))
      .map(x => CardName(x.assume[CardNameConstraint]))

  private def sized(size: Int): Gen[Long] =
    def go(s: Int, acc: String): Gen[Long] =
      Gen.oneOf(1 to 9).flatMap { n =>
        if s == size then acc.toLong
        else go(s + 1, acc + n.toString)
      }

    go(0, "")

  val cardGen: Gen[Card] =
    for
      n <- cardNameGen
      u <- sized(16).map(x => CardNumber(x.assume[Size[16]]))
      x <- sized(4).map(x =>
             CardExpiration(x.toString.assume[CardExpirationConstraint])
           )
      c <- sized(3).map(x => CardCVV(x.toInt.assume[Size[3]]))
    yield Card(n, u, x, c)

  val paymentGen: Gen[Payment] =
    for
      i <- userIdGen
      t <- priceGen
      c <- cardGen
    yield Payment(i, t, c)

  val orderGen: Gen[Order] =
    for
      oid   <- orderIdGen
      pid   <- paymentIdGen
      items <- Gen.mapOf(itemMapGen)
      t     <- priceGen
    yield Order(oid, pid, items, t)

end generators
