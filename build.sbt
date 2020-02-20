
/*
 build.sbt adapted from https://github.com/pbassiner/sbt-multi-project-example/blob/master/build.sbt
*/


name := "bwhc-medication-catalog"
organization in ThisBuild := "de.bwhc"
//scalaVersion in ThisBuild := "2.13.0"
scalaVersion in ThisBuild := "2.12.8"
version in ThisBuild:= "1.0"


//-----------------------------------------------------------------------------
// PROJECTS
//-----------------------------------------------------------------------------

lazy val global = project
  .in(file("."))
  .settings(settings)
  .aggregate(
     medication_catalog_api,
     medication_catalog_impl,
     tests
  )



lazy val medication_catalog_api = project
  .settings(
    name := "medication-catalog-api",
    settings
  )

lazy val medication_catalog_impl = project
  .settings(
    name := "medication-catalog-impl",
    settings
  )
  .dependsOn(medication_catalog_api)

lazy val tests = project
  .settings(
    name := "tests",
    settings,
    libraryDependencies ++= Seq(
      dependencies.scalatest % "test"
    )
  )
  .dependsOn(
    medication_catalog_api,
    medication_catalog_impl % "test"
  )



//-----------------------------------------------------------------------------
// DEPENDENCIES
//-----------------------------------------------------------------------------

lazy val dependencies =
  new {
    val scalatest  = "org.scalatest"     %% "scalatest"        % "3.0.8"
    val slf4j      = "org.slf4j"         %  "slf4j-api"        % "1.7.26"
    val logback    = "ch.qos.logback"    %  "logback-classic"  % "1.0.13"
//    val play_json  = "com.typesafe.play" %% "play-json"        % "2.7.0"
  }

lazy val commonDependencies = Seq(
  dependencies.slf4j,
  dependencies.scalatest % "test",
)


//-----------------------------------------------------------------------------
// SETTINGS
//-----------------------------------------------------------------------------

lazy val settings = commonSettings


lazy val compilerOptions = Seq(
  "-unchecked",
//  "-feature",
//  "-language:existentials",
//  "-language:higherKinds",
//  "-language:implicitConversions",
//  "-language:postfixOps",
  "-deprecation",
  "-encoding",
  "utf8"
)

lazy val commonSettings = Seq(
  scalacOptions ++= compilerOptions,
  resolvers ++= Seq(
    "Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository",
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots")
  )
)

