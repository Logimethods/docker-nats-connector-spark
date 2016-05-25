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

imageNames in docker := Seq(
  // Sets the latest tag
  ImageName(s"${organization.value}/${name.value}:latest"),

  // Sets a name with a tag that contains the project version
  ImageName(
    namespace = Some(organization.value),
    repository = name.value,
    tag = Some("v" + version.value)
  )
)

// Define a Dockerfile
dockerfile in docker := {
  val classpath = (managedClasspath in Compile).value

  new Dockerfile {
    // Use a base image that contain Gatling
	from("denvazh/gatling:2.1.7")
    // Add all files on the classpath
    add(classpath.files, "./lib/")
    // Add Gatling User Files
    add(baseDirectory.value / "user-files", "./user-files")
    // Add Scripts
    add(baseDirectory.value / "scripts", "/scripts")
    
    cmd("/scripts/starts_nats_injection.sh")
  }
}