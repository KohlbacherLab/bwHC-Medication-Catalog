
/*
 build.sbt adapted from https://github.com/pbassiner/sbt-multi-project-example/blob/master/build.sbt
*/


name := "bwhc-medication-catalog"
ThisBuild / organization := "de.bwhc"
ThisBuild / version      := "1.0-SNAPSHOT"

lazy val scala213 = "2.13.8"
lazy val supportedScalaVersions =
  List(
    scala213
  )

ThisBuild / scalaVersion := scala213


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
    libraryDependencies ++= Seq(
      dependencies.play_json
    ),
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
    impl % Test
  )



//-----------------------------------------------------------------------------
// DEPENDENCIES
//-----------------------------------------------------------------------------

lazy val dependencies =
  new {
    val scalatest  = "org.scalatest"     %% "scalatest"        % "3.0.8" % Test
    val play_json  = "com.typesafe.play" %% "play-json"        % "2.8.1"
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
  resolvers ++=
    Seq("Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository") ++
    Resolver.sonatypeOssRepos("releases") ++
    Resolver.sonatypeOssRepos("snapshots")
)

