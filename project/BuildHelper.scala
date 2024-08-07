import com.typesafe.sbt.SbtNativePackager.Docker
import com.typesafe.sbt.packager.Keys.*
import org.scalafmt.sbt.ScalafmtPlugin.autoImport.scalafmtOnCompile
import sbt.Keys.*
import sbt.*

object BuildHelper {
  val scala3 = "3.3.0"

  def dockerSettings: List[Setting[? >: String & Seq[Int] & Boolean]] = List(
    Docker / packageName := "shopping-cart-app",
    dockerBaseImage := "openjdk:11-jre-slim-buster",
    dockerExposedPorts ++= List(8080),
    dockerUpdateLatest := true
  )

  def nameSettings: List[Setting[String]] = List(
    name := "shopping-cart-app",
  )

  def organizationSettings: List[Setting[String]] = List(
    organization := "com.kevchuang",
    organizationName := "kevchuang"
  )

  def standardSettings
  : List[Setting[? >: String & Task[Seq[String]] & Boolean & Seq[TestFramework]]] = List(
    ThisBuild / scalaVersion := scala3,
    scalacOptions := ScalaSettings.baseSettings,
    scalafmtOnCompile := true,
    Test / parallelExecution := true,
    ThisBuild / fork := true,
    run / fork := true,
    testFrameworks += new TestFramework("weaver.framework.CatsEffect")
  )
}