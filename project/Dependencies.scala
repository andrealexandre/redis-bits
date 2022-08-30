import sbt._

object Dependencies {
  lazy val slf4j = "org.slf4j" % "slf4j-api" % "1.7.30"
  lazy val logback = "ch.qos.logback" % "logback-classic" % "1.2.3"
  lazy val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % "3.9.3"

  lazy val lettuce = "io.lettuce" % "lettuce-core" % "6.2.0.RELEASE"

  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.2.8"
  lazy val testContainers = "org.testcontainers" % "testcontainers" % "1.15.3"
}