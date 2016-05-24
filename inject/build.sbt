//@see https://github.com/marcuslonnberg/sbt-docker
//@see https://github.com/marcuslonnberg/sbt-docker/blob/master/examples/package-spray/build.sbt
//@see https://velvia.github.io/Docker-Scala-Sbt/

import sbt.Keys.{artifactPath, libraryDependencies, mainClass, managedClasspath, name, organization, packageBin, resolvers, version}

logLevel := Level.Debug

name := "docker-nats-connector-spark-inject"

organization := "logimethods"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq("com.logimethods" %% "nats-connector-gatling" % version.value)

resolvers += Resolver.mavenLocal

enablePlugins(DockerPlugin)

// Define a Dockerfile
dockerfile in docker := {
  new Dockerfile {
    // Use a base image that contain Scala
	from("denvazh/gatling:2.1.7")

	cmd("bash")
  }
}