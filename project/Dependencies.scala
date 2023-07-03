import sbt.*

object Dependencies {
  private val catsCoreVersion = "2.9.0"
  private val catsEffectVersion = "3.5.0"
  private val cirisVersion = "3.2.0"
  private val fs2Version = "3.7.0"
  private val http4sVersion = "0.23.20"
  private val monocleVersion = "3.2.0"
  private val skunkVersion = "0.6.0-RC2"
  private val refinedVersion = "0.11.0"
  private val tapirVersion = "1.5.4"

  val cats: List[ModuleID] = List(
    "org.typelevel" %% "cats-core" % catsCoreVersion,
    "org.typelevel" %% "cats-effect" % catsEffectVersion
  )

  val ciris: List[ModuleID] = List(
    "is.cir" %% "ciris" % cirisVersion,
    "is.cir" %% "ciris-refined" % cirisVersion
  )

  val fs2: List[ModuleID] = List(
    "co.fs2" %% "fs2-core" % fs2Version
  )

  val http4s: List[ModuleID] = List(
    "org.http4s" %% "http4s-dsl" % http4sVersion,
    "org.http4s" %% "http4s-ember-server" % http4sVersion
  )

  val refined: List[ModuleID] = List(
    "eu.timepit" %% "refined-cats" % refinedVersion,
    "eu.timepit" %% "refined" % refinedVersion
  )

  val skunk: List[ModuleID] = List(
    "org.tpolecat" %% "skunk-core" % skunkVersion
  )

  val tapir: List[ModuleID] = List(
    "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % tapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-prometheus-metrics" % tapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui" % tapirVersion
  )
}