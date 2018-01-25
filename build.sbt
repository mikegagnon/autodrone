import Dependencies._

libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.2"

libraryDependencies += "org.scala-js" %%% "scala-parser-combinators" % "1.0.2"
libraryDependencies += "org.querki" %%% "jquery-facade" % "1.2"
libraryDependencies += "org.scalatest" %%% "scalatest" % "3.0.0" % "test"

resolvers += sbt.Resolver.bintrayRepo("denigma", "denigma-releases")
libraryDependencies += "org.denigma" %%% "codemirror-facade" % "5.13.2-0.8"

enablePlugins(ScalaJSPlugin)

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "me.michaelgagnon",
      scalaVersion := "2.11.0",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "Chopper",
    libraryDependencies += scalaTest % Test
  )

scalaJSUseMainModuleInitializer := true

scalacOptions in ThisBuild ++= Seq("-unchecked", "-deprecation", "-feature")

scalaJSUseRhino in Global := true
