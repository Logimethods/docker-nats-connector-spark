//@see https://github.com/marcuslonnberg/sbt-docker
//@see https://github.com/marcuslonnberg/sbt-docker/blob/master/examples/package-spray/build.sbt
//@see https://velvia.github.io/Docker-Scala-Sbt/

import sbt.Keys.{artifactPath, libraryDependencies, mainClass, managedClasspath, name, organization, packageBin, resolvers, version}

logLevel := Level.Debug

name := "docker-nats-connector-spark-app"

organization := "logimethods"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.10.4"

libraryDependencies += "com.logimethods" % "nats-connector-spark" % "0.1.0-SNAPSHOT"
//libraryDependencies += "io.nats" % "jnats" % "0.4.1"
libraryDependencies += "com.github.tyagihas" % "scala_nats_2.10" % "0.1"

resolvers += Resolver.mavenLocal

enablePlugins(DockerPlugin)

// Define a Dockerfile
dockerfile in docker := {
  val jarFile: File = sbt.Keys.`package`.in(Compile, packageBin).value
  val classpath = (managedClasspath in Compile).value
  val mainclass = mainClass.in(Compile, packageBin).value.getOrElse(sys.error("Expected exactly one main class"))
  val jarTarget = s"/app/${jarFile.getName}"
  // Make a colon separated classpath with the JAR file
  val classpathString = classpath.files.map("/app/" + _.getName)
    .mkString(":") + ":" + jarTarget

  new Dockerfile {
    // Use a base image that contain Scala
	from("williamyeh/scala:2.10.4")
	
    // Add all files on the classpath
    add(classpath.files, "/app/")
    // Add the JAR file
    add(jarFile, jarTarget)
    // On launch run Scala with the classpath and the main class
    entryPoint("scala", "-cp", classpathString, mainclass)
  }
}