import BuildHelper.*

lazy val root = (project in file("."))
  .settings(
    name := "shopping-cart"
  )
  .aggregate(core, tests)

lazy val tests = (project in file("modules/tests"))
  .configs(IntegrationTest.extend(Test))
  .settings(Defaults.itSettings)
  .settings(standardSettings)
  .settings(
    name := "shopping-cart-test-suite"
  )
  .dependsOn(core)

lazy val core = (project in file("modules/core"))
  .enablePlugins(AshScriptPlugin)
  .enablePlugins(DockerPlugin)
  .settings(name := "shopping-cart-core")
  .settings(organizationSettings)
  .settings(standardSettings)
  .settings(dockerSettings)
  .settings(
    libraryDependencies ++=
      Dependencies.cats ++
        Dependencies.circe ++
        Dependencies.ciris ++
        Dependencies.fs2 ++
        Dependencies.http4s ++
        Dependencies.http4sJwtAuth ++
        Dependencies.iron ++
        Dependencies.kittens ++
        Dependencies.log4cats ++
        Dependencies.redis4cats ++
        Dependencies.skunk ++
        Dependencies.weaver
  )
