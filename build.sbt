
lazy val commonSettings = Seq(
  organization := "com.opi.lil",
  name := "scala-test",
  version := "1.0",
  scalaVersion := "2.12.8",
  Compile/mainClass := Some("MainApp")
)

lazy val root = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor" % "2.6.0-RC2",
      "com.typesafe.akka" %% "akka-slf4j" % "2.6.0-RC2",
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "com.datastax.cassandra" % "cassandra-driver-core" % "3.7.1"
    ),
    resolvers += "gphat" at "https://raw.github.com/gphat/mvn-repo/master/releases/",
    assemblyMergeStrategy in assembly := {
      case PathList("META-INF", _*) => MergeStrategy.discard
      case x => MergeStrategy.first
    }
  )

mainClass in assembly := Some("MainApp")