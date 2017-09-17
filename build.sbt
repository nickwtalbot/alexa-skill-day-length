name := "daylength"

organization := "com.nicktalbot.alexa"

version := "0.1"

scalaVersion := "2.12.3"

scalacOptions ++= Seq("-feature", "-unchecked", "-deprecation")

libraryDependencies ++= Seq(
  "com.amazon.alexa" % "alexa-skills-kit" % "1.5.0",
  "com.amazonaws" % "aws-lambda-java-log4j" % "1.0.0",
  "com.typesafe.akka" %% "akka-http" % "10.0.10",
  "org.json4s" %% "json4s-native" % "3.5.3",
  "org.clapper" %% "grizzled-slf4j" % "1.3.1",

  "org.scalatest" %% "scalatest" % "3.0.4" % "test"
)

assemblyJarName in assembly := "daylength-" + version.value + ".jar"
