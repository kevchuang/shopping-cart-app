package com.kevchuang.shop.services

import cats.effect.*
import cats.syntax.all.*
import cats.effect.std.UUIDGen
import com.kevchuang.shop.domain.category.*
import com.kevchuang.shop.sql.codecs.*
import skunk.*
import skunk.implicits.*
import io.github.iltotore.iron.*

trait Categories[F[_]]:
  def findAll: F[List[Category]]
  def create(name: CategoryName): F[CategoryId]
end Categories

object Categories:

  def make[F[_]: UUIDGen: MonadCancelThrow](
      postgres: Resource[F, Session[F]]
  ): Categories[F] =
    new Categories[F]:
      import CategoriesSQL.*

      def findAll: F[List[Category]] =
        postgres.use(_.execute(selectCategories))

      def create(name: CategoryName): F[CategoryId] =
        postgres.use: session =>
          for
            preparedCommand <- session.prepare(insertCategory)
            categoryId      <- UUIDGen.randomUUID[F].map(CategoryId(_))
            _               <- preparedCommand.execute(Category(categoryId, name))
          yield categoryId

end Categories

private object CategoriesSQL:
  private val categoryCodec: Codec[Category] =
    (categoryId ~ categoryName).imap { case id ~ name =>
      Category(id, name)
    }(c => (c.uuid, c.name))

  val selectCategories: Query[Void, Category] =
    sql"""
         SELECT * FROM categories
       """.query(categoryCodec)

  val insertCategory: Command[Category] =
    sql"""
         INSERT INTO categories 
         VALUES ($categoryCodec)
         """.command
end CategoriesSQL
