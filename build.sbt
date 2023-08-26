import BuildHelper.*

lazy val root = (project in file("."))
  .enablePlugins(AshScriptPlugin)
  .enablePlugins(DockerPlugin)
  .settings(nameSettings)
  .settings(standardSettings)
  .settings(dockerSettings)
  .configs(IntegrationTest.extend(Test))
  .settings(Defaults.itSettings)
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
        Dependencies.skunk ++
        Dependencies.weaver
  )
