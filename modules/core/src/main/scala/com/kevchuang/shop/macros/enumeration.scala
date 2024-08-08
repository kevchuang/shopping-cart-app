package com.kevchuang.shop.macros

import io.circe.{Decoder, Encoder}

import scala.compiletime.summonAll
import scala.deriving.Mirror

object enumeration:
  inline def stringEnumDecoder[T](using m: Mirror.SumOf[T]): Decoder[T] =
    val elemInstances =
      summonAll[Tuple.Map[m.MirroredElemLabels, ValueOf]].productIterator
        .asInstanceOf[Iterator[ValueOf[T]]]
        .map(_.value)
    val elemNames =
      summonAll[Tuple.Map[m.MirroredElemLabels, ValueOf]].productIterator
        .asInstanceOf[Iterator[ValueOf[String]]]
        .map(_.value)
    val mapping = (elemNames zip elemInstances).toMap
    Decoder[String].emap { name =>
      mapping.get(name).fold(Left(s"Name $name is invalid value"))(Right(_))
    }

  inline def stringEnumEncoder[T](using m: Mirror.SumOf[T]): Encoder[T] =
    val elemInstances =
      summonAll[Tuple.Map[m.MirroredElemTypes, ValueOf]].productIterator
        .asInstanceOf[Iterator[ValueOf[T]]]
        .map(_.value)
    val elemNames =
      summonAll[Tuple.Map[m.MirroredElemLabels, ValueOf]].productIterator
        .asInstanceOf[Iterator[ValueOf[String]]]
        .map(_.value)
    val mapping = (elemInstances zip elemNames).toMap
    Encoder[String].contramap[T](mapping.apply)

end enumeration
