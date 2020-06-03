name := "SCALA project"

version := "0.1"

scalaVersion := "2.12.11"

addSbtPlugin("io.get-coursier" % "sbt-coursier" % "2.0.0-RC3-3")
libraryDependencies += "com.typesafe.slick" %% "slick" % "3.3.2"
libraryDependencies += "org.slf4j" % "slf4j-nop" % "1.7.26"
libraryDependencies += "com.h2database" % "h2" % "1.4.192"
libraryDependencies += "org.scala-lang.modules" %% "scala-swing" % "2.1.1"
