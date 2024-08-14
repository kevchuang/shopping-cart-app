import sbt.*

object Dependencies {
  private val catsCoreVersion = "2.12.0"
  private val catsEffectVersion = "3.5.0"
  private val circeVersion = "0.14.9"
  private val cirisVersion = "3.2.0"
  private val fs2Version = "3.10.2"
  private val http4sVersion = "0.23.20"
  private val http4sJwtVersion = "1.2.0"
  private val ironVersion = "2.6.0"
  private val kittenVersion = "3.0.0"
  private val log4CatsVersion = "2.6.0"
  private val redis4CatsVersion = "1.4.3"
  private val monocleVersion = "3.2.0"
  private val skunkVersion = "0.6.0-RC2"
  private val squantsVersion = "1.8.3"
  private val weaverVersion = "0.8.3"

  val cats: List[ModuleID] = List(
    "org.typelevel" %% "cats-core" % catsCoreVersion,
    "org.typelevel" %% "cats-effect" % catsEffectVersion
  )

  val circe: List[ModuleID] = List(
    "io.circe" %% "circe-core" % circeVersion,
    "io.circe" %% "circe-generic" % circeVersion,
    "io.circe" %% "circe-parser" % circeVersion
  )

  val ciris: List[ModuleID] = List(
    "is.cir" %% "ciris" % cirisVersion,
    "is.cir" %% "ciris-refined" % cirisVersion
  )

  val fs2: List[ModuleID] = List(
    "co.fs2" %% "fs2-core" % fs2Version
  )

  val http4s: List[ModuleID] = List(
    "org.http4s" %% "http4s-circe" % http4sVersion,
    "org.http4s" %% "http4s-dsl" % http4sVersion,
    "org.http4s" %% "http4s-ember-server" % http4sVersion,
    "org.http4s" %% "http4s-ember-client" % http4sVersion
  )

  val http4sJwtAuth: List[ModuleID] = List(
    "dev.profunktor" %% "http4s-jwt-auth" % http4sJwtVersion
  )

  val iron: List[ModuleID] = List(
    "io.github.iltotore" %% "iron" % ironVersion,
    "io.github.iltotore" %% "iron-cats" % ironVersion,
    "io.github.iltotore" %% "iron-circe" % ironVersion,
    "io.github.iltotore" %% "iron-ciris" % ironVersion
  )

  val kittens: List[ModuleID] = List(
    "org.typelevel" %% "kittens" % kittenVersion
  )

  val log4cats: List[ModuleID] = List(
    "org.typelevel" %% "log4cats-slf4j" % log4CatsVersion
  )

  val redis4cats: List[ModuleID] = List(
    "dev.profunktor" %% "redis4cats-effects" % redis4CatsVersion,
    "dev.profunktor" %% "redis4cats-log4cats" % redis4CatsVersion
  )

  val skunk: List[ModuleID] = List(
    "org.tpolecat" %% "skunk-core" % skunkVersion,
    "org.tpolecat" %% "skunk-circe" % skunkVersion
  )

  val squants: List[ModuleID] = List(
    "org.typelevel" %% "squants" % squantsVersion
  )

  val weaver: List[ModuleID] = List(
    "com.disneystreaming" %% "weaver-cats" % weaverVersion,
    "com.disneystreaming" %% "weaver-scalacheck" % weaverVersion
  )
}