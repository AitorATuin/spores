resolvers += "Typesafe Snapshots" at "http://repo.typesafe.com/typesafe/snapshots/"

resolvers += "iBlibio" at "http://http://mirrors.ibiblio.org/maven2/"
 
resolvers += "spray repo" at "http://repo.spray.io"

libraryDependencies ++= Seq(
  "org.scalaz" % "scalaz-core_2.10" % "7.0.0-M7", //cross CrossVersion.full
  "org.scalaz" % "scalaz-iteratee_2.10" % "7.0.0-M7",
  "org.scalaz" % "scalaz-effect_2.10" % "7.0.0-M7",
  "io.spray" % "spray-can" % "1.1-M7",
  "io.spray" % "spray-http" % "1.1-M7",
  "io.spray" % "spray-httpx" % "1.1-M7",
  "io.spray" % "spray-util" % "1.1-M7",
  "net.databinder.dispatch" %% "dispatch-core" % "0.9.5",
  "net.liftweb" %% "lift-json" % "2.5-M4"
)

initialCommands in console := "import scalaz._, Scalaz._"

