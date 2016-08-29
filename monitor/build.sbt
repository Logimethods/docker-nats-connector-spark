//@see https://github.com/marcuslonnberg/sbt-docker
//@see https://github.com/marcuslonnberg/sbt-docker/blob/master/examples/package-spray/build.sbt
//@see https://velvia.github.io/Docker-Scala-Sbt/

import sbt.Keys.{artifactPath, libraryDependencies, mainClass, managedClasspath, name, organization, packageBin, resolvers, version}

logLevel := Level.Debug

val rootName = "nats-connector-spark"
name := "docker-" + rootName + "-monitor"
organization := "logimethods"
val tag = "monitor"

version := "0.2.0-SNAPSHOT"
scalaVersion := "2.10.6"

libraryDependencies += "com.github.tyagihas" % "scala_nats_2.10" % "0.1"
libraryDependencies += "io.nats"     		 % "java-nats-streaming" % "v0.1.0"

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
resolvers += "Sonatype OSS Release" at "https://oss.sonatype.org/content/groups/public/"
// TODO Remove once the nats-parent 1.0-SNAPSHOT is fixed (https://github.com/nats-io/java-nats-streaming/issues/18)
resolvers += "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository"

// @see http://stackoverflow.com/questions/30446984/spark-sbt-assembly-deduplicate-different-file-contents-found-in-the-followi
assemblyMergeStrategy in assembly := {
    case "nats_checkstyle.xml" => MergeStrategy.last
    case x =>
        val oldStrategy = (assemblyMergeStrategy in assembly).value
        oldStrategy(x)
}

enablePlugins(DockerPlugin)

imageNames in docker := Seq(
  ImageName(s"${organization.value}/${rootName}:${tag}")
)

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

// sbt dockerFileTask
// See https://github.com/marcuslonnberg/sbt-docker/issues/34

val dockerFileTask = taskKey[Unit]("Prepare the dockerfile and needed files")

dockerFileTask := {
  val dockerDir = target.value / "docker"
  val artifact: File = assembly.value
  val artifactTargetPath = s"/app/${artifact.name}"

  val jarFile: File = sbt.Keys.`package`.in(Compile, packageBin).value
  val classpath = (managedClasspath in Compile).value
  val mainclass = mainClass.in(Compile, packageBin).value.getOrElse(sys.error("Expected exactly one main class"))
  val jarTarget = s"/app/${jarFile.getName}"
  // Make a colon separated classpath with the JAR file
  val classpathString = classpath.files.map("/app/" + _.getName)
    .mkString(":") + ":" + jarTarget

  val dockerFile = new Dockerfile {
    // Use a base image that contain Scala
	from("williamyeh/scala:2.10.4")
	
    // Add all files on the classpath
    add(classpath.files, "/app/")
    // Add the JAR file
    add(jarFile, jarTarget)
    // On launch run Scala with the classpath and the main class
    entryPoint("scala", "-cp", classpathString, mainclass)
  }

  val stagedDockerfile =  sbtdocker.staging.DefaultDockerfileProcessor(dockerFile, dockerDir)
  IO.write(dockerDir / "Dockerfile",stagedDockerfile.instructionsString)
  stagedDockerfile.stageFiles.foreach {
    case (source, destination) =>
      source.stage(destination)
  }
}

dockerFileTask <<= dockerFileTask.dependsOn(compile in Compile, dockerfile in docker)