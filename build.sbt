
/*
 build.sbt adapted from https://github.com/pbassiner/sbt-multi-project-example/blob/master/build.sbt
*/


name := "bwhc-medication-catalog"
organization in ThisBuild := "de.bwhc"
version in ThisBuild:= "1.0-SNAPSHOT"

lazy val scala212 = "2.12.10"
lazy val scala213 = "2.13.1"
lazy val supportedScalaVersions =
  List(
    scala212,
    scala213
  )

scalaVersion in ThisBuild := scala213


//-----------------------------------------------------------------------------
// PROJECTS
//-----------------------------------------------------------------------------

lazy val global = project
  .in(file("."))
  .settings(
    settings,
    crossScalaVersions := Nil,
    publish / skip := true
  )
  .aggregate(
    api,
    impl,
    tests
  )

lazy val api = project
  .settings(
    name := "medication-catalog-api",
    settings,
    crossScalaVersions := supportedScalaVersions
  )

lazy val impl = project
  .settings(
    name := "medication-catalog-impl",
    settings,
    crossScalaVersions := supportedScalaVersions
  )
  .dependsOn(api)

lazy val tests = project
  .settings(
    name := "tests",
    settings,
    libraryDependencies ++= Seq(
      dependencies.scalatest
    ),
    crossScalaVersions := supportedScalaVersions,
    publish / skip := true
  )
  .dependsOn(
    api,
    impl % "test"
  )



//-----------------------------------------------------------------------------
// DEPENDENCIES
//-----------------------------------------------------------------------------

lazy val dependencies =
  new {
    val scalatest  = "org.scalatest"     %% "scalatest"        % "3.0.8" % "test"
    val slf4j      = "org.slf4j"         %  "slf4j-api"        % "1.7.26"
    val play_json  = "com.typesafe.play" %% "play-json"        % "2.8.0"
  }


//-----------------------------------------------------------------------------
// SETTINGS
//-----------------------------------------------------------------------------

lazy val settings = commonSettings


lazy val compilerOptions = Seq(
  "-unchecked",
  "-feature",
  "-Xfatal-warnings",
  "-deprecation",
  "-encoding", "utf8"
)

lazy val commonSettings = Seq(
  scalacOptions ++= compilerOptions,
  resolvers ++= Seq(
    "Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository",
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots")
  )
)

