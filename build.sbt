ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

lazy val root = (project in file("."))
  .settings(
    name := "http4s-studies"
  )

val Http4sVersion = "1.0.0-M21"
val CirceVersion = "0.14.4"
val ScalaTestVersion = "3.2.15"
val WeaverTestVersion = "0.8.1"

libraryDependencies ++= Seq(
  "org.http4s"      %% "http4s-ember-server" % Http4sVersion,
  "org.http4s"      %% "http4s-ember-client" % Http4sVersion,

  "org.http4s"      %% "http4s-blaze-server" % Http4sVersion,
  "org.http4s"      %% "http4s-circe"        % Http4sVersion,
  "org.http4s"      %% "http4s-dsl"          % Http4sVersion,

  "org.http4s" %% "http4s-circe" % Http4sVersion,
  // Optional for auto-derivation of JSON codecs
  "io.circe" %% "circe-generic" % CirceVersion,
  // Optional for string interpolation to JSON model
  "io.circe" %% "circe-literal" % CirceVersion,

  // testing
  "org.scalatest" %% "scalatest" % ScalaTestVersion % Test,
  "org.scalatestplus" %% "mockito-4-6" % "3.2.15.0" % "test",

  "com.disneystreaming" %% "weaver-cats" % WeaverTestVersion % Test,
  "com.disneystreaming" %% "weaver-scalacheck" % WeaverTestVersion % Test
)

testFrameworks += new TestFramework("weaver.framework.CatsEffect")
// Uncomment if you're using Scala 2.12.x
// scalacOptions ++= Seq("-Ypartial-unification")
