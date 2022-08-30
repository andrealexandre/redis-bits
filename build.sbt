import Dependencies.{testContainers, _}

scalaVersion := "2.13.8"
organizationName := "aalexandre"
organization := "org.aalexandre"

name := "redis-bits"
version := "1.0"

libraryDependencies ++= Seq(
  slf4j,
  logback,
  scalaLogging,
  lettuce,
  redisScala,
  scalaTest % Test,
  testContainers % Test
)
