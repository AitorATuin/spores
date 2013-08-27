import sbt._
import sbt.Keys._

object SporesBuild extends Build {

  lazy val spores = Project(
    id = "spores",
    base = file("."),
    settings = Project.defaultSettings ++ Seq(
      name := "SporeS",
      organization := "com.logikujo",
      version := "0.1-SNAPSHOT",
      scalaVersion := "2.10.0"
      // add other settings here
    )
  )
}
