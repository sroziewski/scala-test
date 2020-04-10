name := "scala-test"

version := "0.1"

scalaVersion := "2.12.8"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.6.0-RC2",
  "com.typesafe.akka" %% "akka-slf4j" % "2.6.0-RC2",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.datastax.cassandra" % "cassandra-driver-core" % "3.7.1"
)
