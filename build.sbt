val ScalatraVersion = "2.6.4"

name := "similarArtist"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.12.7"

resolvers += Classpaths.typesafeReleases

libraryDependencies ++= Seq(
  "org.scalatra" %% "scalatra" % ScalatraVersion,
  "org.scalatra" %% "scalatra-scalatest" % ScalatraVersion % "test",
  "ch.qos.logback" % "logback-classic" % "1.2.3" % "runtime",
  "org.eclipse.jetty" % "jetty-webapp" % "9.4.9.v20180320" % "container",
  "javax.servlet" % "javax.servlet-api" % "3.1.0" % "provided",
  "org.neo4j.driver" % "neo4j-java-driver" % "1.7.1"
)

enablePlugins(SbtTwirl)
enablePlugins(ScalatraPlugin)
