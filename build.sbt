import Dependencies._

libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.2"

libraryDependencies += "org.scala-js" %%% "scala-parser-combinators" % "1.0.2"



resolvers += sbt.Resolver.bintrayRepo("denigma", "denigma-releases") //add resolver
libraryDependencies += "org.denigma" %%% "codemirror-facade" % "5.13.2-0.8" //add dependency

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