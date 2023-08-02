package com.kevchuang.shop.services

import cats.effect.Resource
import cats.effect.kernel.MonadCancelThrow
import cats.effect.std.UUIDGen
import cats.syntax.all.*
import com.kevchuang.shop.domain.brand.*
import com.kevchuang.shop.domain.category.*
import com.kevchuang.shop.domain.item.*
import com.kevchuang.shop.sql.codecs.*
import skunk.*
import skunk.implicits.*

trait Items[F[_]]:
  def create(item: CreateItem): F[ItemId]
  def findAll: F[List[Item]]
  def findByBrand(brand: BrandName): F[List[Item]]
end Items

object Items:

  def make[F[_]: UUIDGen: MonadCancelThrow](
      postgres: Resource[F, Session[F]]
  ): Items[F] =
    new Items[F]:
      import ItemsSQL.*

      def create(item: CreateItem): F[ItemId] = postgres.use { session =>
        for
          preparedCommand <- session.prepare(insertItem)
          itemId          <- UUIDGen.randomUUID[F].map(ItemId(_))
          _               <- preparedCommand.execute(itemId, item)
        yield itemId
      }

      def findAll: F[List[Item]] =
        postgres.use(_.execute(selectItems))

      def findByBrand(brand: BrandName): F[List[Item]] =
        postgres.use(_.execute(selectItemsByBrand)(brand))

end Items

private object ItemsSQL:
  private val itemDecoder: Decoder[Item] =
    (itemId ~ itemName ~ itemDescription ~ price ~ brandId ~ brandName ~ categoryId ~ categoryName)
      .map { case i ~ n ~ d ~ p ~ bi ~ bn ~ ci ~ cn =>
        Item(i, n, d, p, Brand(bi, bn), Category(ci, cn))
      }

  val selectItems: Query[Void, Item] =
    sql"""
        SELECT i.uuid, i.name, i.description, i.price, b.uuid, b.name, c.uuid, c.name
        FROM items AS i
        INNER JOIN brands AS b ON i.brand_id = b.uuid
        INNER JOIN categories AS c ON i.category_id = c.uuid
       """.query(itemDecoder)

  val selectItemsByBrand: Query[BrandName, Item] =
    sql"""
        SELECT i.uuid, i.name, i.description, i.price, b.uuid, b.name, c.uuid, c.name
        FROM items AS i
        INNER JOIN brands AS b ON i.brand_id = b.uuid
        INNER JOIN categories AS c ON i.category_id = c.uuid
        WHERE b.name LIKE $brandName
      """.query(itemDecoder)

  val insertItem: Command[ItemId ~ CreateItem] =
    sql"""
        INSERT INTO items
        VALUES ($itemId, $itemName, $itemDescription, $price, $brandId, $categoryId)
       """.command.contramap { case id ~ i =>
      (id, i.name, i.description, i.price, i.brandId, i.categoryId)
    }
end ItemsSQL
