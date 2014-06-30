import sbt._
import sbt.Keys._
import BuildSettings._

//TODO: Add support for renaming files before copying
//TODO: Add support for modify files before going to jar. Change files to production mode
object Server extends Build with WithDependencies with WithResolvers {
  lazy val module = Project("spores", file(".")).
    settings(appSettings: _*).
    settings(libraryDependencies ++= dependenciesBuild).
    settings(resolvers ++= resolversBuild)
}
