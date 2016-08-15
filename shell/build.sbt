//@see https://github.com/marcuslonnberg/sbt-docker
//@see https://github.com/marcuslonnberg/sbt-docker/blob/master/examples/package-spray/build.sbt
//@see https://velvia.github.io/Docker-Scala-Sbt/

import sbt.Keys.{artifactPath, libraryDependencies, mainClass, managedClasspath, name, organization, packageBin, resolvers, version}

logLevel := Level.Debug

name := "docker-nats-connector-spark"
organization := "logimethods"
val tag = "shell"

version := "0.1.0"

libraryDependencies += "com.logimethods" % "nats-connector-spark" % "0.1.0"

resolvers += Resolver.mavenLocal

enablePlugins(DockerPlugin)

imageNames in docker := Seq(
  ImageName(s"${organization.value}/${name.value}:${tag}")
)

// Define a Dockerfile
dockerfile in docker := {
  val jarFile: File = sbt.Keys.`package`.in(Compile, packageBin).value
  val classpath = (managedClasspath in Compile).value
  val jarTarget = s"./lib_main/${jarFile.getName}"
  // Make a colon separated classpath with the JAR file
  val classpathString = classpath.files.map("./lib_add/" + _.getName).mkString(":") + ":" + jarTarget

  new Dockerfile {
    // Use a base image that contain Spark
    from("gettyimages/spark:1.6.1-hadoop-2.6")

    // Add all files on the classpath
    add(classpath.files, "./lib_add/")
    
    // Add the JAR file
    add(jarFile, jarTarget)

    // Copy all Spark scripts
    add(baseDirectory.value / "script", "./script")

    // Copy all commands
    add(baseDirectory.value / "command", "./")

    env("SPARK_LIBS_PATHS", classpathString)
    cmd("sleep", "infinity")
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
  val jarTarget = s"./lib_main/${jarFile.getName}"
  // Make a colon separated classpath with the JAR file
  val classpathString = classpath.files.map("./lib_add/" + _.getName).mkString(":") + ":" + jarTarget

  val dockerFile = new Dockerfile {
    // Use a base image that contain Spark
    from("gettyimages/spark:1.6.1-hadoop-2.6")

    // Add all files on the classpath
    add(classpath.files, "./lib_add/")
    
    // Add the JAR file
    add(jarFile, jarTarget)

    // Copy all Spark scripts
    add(baseDirectory.value / "script", "./script")

    // Copy all commands
    add(baseDirectory.value / "command", "./")

    env("SPARK_LIBS_PATHS", classpathString)
    cmd("sleep", "infinity")
  }

  val stagedDockerfile =  sbtdocker.staging.DefaultDockerfileProcessor(dockerFile, dockerDir)
  IO.write(dockerDir / "Dockerfile",stagedDockerfile.instructionsString)
  stagedDockerfile.stageFiles.foreach {
    case (source, destination) =>
      source.stage(destination)
  }
}

dockerFileTask <<= dockerFileTask.dependsOn(compile in Compile, dockerfile in docker)