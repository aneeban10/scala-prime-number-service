import Dependencies._

ThisBuild / scalaVersion     := "2.13.4"

lazy val root = (project in file("."))
  .settings(
    name := "scala-prime-number-service",
    libraryDependencies += scalaTest % Test
  )
