import BuildHelper.*

lazy val root = (project in file("."))
  .enablePlugins(AshScriptPlugin)
  .enablePlugins(DockerPlugin)
  .settings(nameSettings)
  .settings(standardSettings)
  .settings(dockerSettings)
  .settings(
    libraryDependencies ++=
      Dependencies.cats ++
        Dependencies.ciris ++
        Dependencies.fs2 ++
        Dependencies.http4s ++
        Dependencies.refined ++
        Dependencies.skunk ++
        Dependencies.tapir
  )
