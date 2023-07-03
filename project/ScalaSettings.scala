object ScalaSettings {
  val baseSettings: List[String] = List(
    "-Wunused:explicits",
    "-Wunused:implicits",
    "-Wunused:imports",
    "-Wunused:locals",
    "-Wunused:nowarn",
    "-Wunused:params",
    "-Wunused:privates",
    "-Wvalue-discard",
    "-Xfatal-warnings",
    "-encoding",
    "utf8",
    "-explaintypes",
    "-feature",
    "-language:existentials",
    "-language:experimental.macros",
    "-language:higherKinds",
    "-language:implicitConversions",
    "-unchecked",
    "-deprecation"
  )
}