import sbt._
import sbt.Keys._

trait WithDependencies {
  val dependenciesBuild = Seq(
    "org.scalaz" %% "scalaz-core" % "7.0.6",
    "net.databinder.dispatch" %% "dispatch-core" % "0.11.1",
    "io.argonaut" %% "argonaut" % "6.1-M2",
    "org.scalatest" % "scalatest_2.10" % "1.9.1" % "test",
    "org.scalamock" %% "scalamock-scalatest-support" % "3.0.1" % "test",
    "org.mockito" % "mockito-core" % "1.8.5" % "test"
  )
}

trait WithResolvers {
  val resolversBuild = Seq(
  "java m2" at "http://download.java.net/maven/2",
  "sonatype-public" at "https://oss.sonatype.org/content/groups/public",
  "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/",
  "Typesafe Snapshots" at "http://repo.typesafe.com/typesafe/snapshots/"
)
}

object BuildSettings {

  val buildTime = SettingKey[String]("build-time")

  val basicSettings = Defaults.defaultSettings ++ Seq(
    name := "spores",
    version := "0.1-SNAPSHOT",
    organization := "com.logikujo",
    scalaVersion := "2.10.4",
    scalacOptions <<= scalaVersion map { sv: String =>
      if (sv.startsWith("2.10."))
        Seq("-deprecation", "-unchecked", "-feature", "-language:postfixOps", "-language:implicitConversions")
      else
        Seq("-deprecation", "-unchecked")
    },
    javaOptions ++= List(),
    fork in run := false
  )

  val appSettings = basicSettings
}
