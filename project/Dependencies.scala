import sbt.*

object Dependencies {
  private val catsCoreVersion = "2.9.0"
  private val catsEffectVersion = "3.5.0"
  private val circeVersion = "0.14.5"
  private val cirisVersion = "3.2.0"
  private val fs2Version = "3.7.0"
  private val http4sVersion = "0.23.20"
  private val ironVersion = "2.2.0"
  private val kittenVersion = "3.0.0"
  private val monocleVersion = "3.2.0"
  private val skunkVersion = "0.6.0-RC2"
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
    "org.http4s" %% "http4s-ember-server" % http4sVersion
  )

  val iron: List[ModuleID] = List(
    "io.github.iltotore" %% "iron" % ironVersion,
    "io.github.iltotore" %% "iron-cats" % ironVersion,
    "io.github.iltotore" %% "iron-circe" % ironVersion,
  )

  val kittens: List[ModuleID] = List(
    "org.typelevel" %% "kittens" % kittenVersion
  )

  val skunk: List[ModuleID] = List(
    "org.tpolecat" %% "skunk-core" % skunkVersion
  )

  val weaver: List[ModuleID] = List(
    "com.disneystreaming" %% "weaver-cats" % weaverVersion % Test,
    "com.disneystreaming" %% "weaver-scalacheck" % weaverVersion % Test
  )
}