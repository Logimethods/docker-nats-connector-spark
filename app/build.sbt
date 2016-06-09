//@see https://github.com/marcuslonnberg/sbt-docker
//@see https://github.com/marcuslonnberg/sbt-docker/blob/master/examples/package-spray/build.sbt
//@see https://velvia.github.io/Docker-Scala-Sbt/

import sbt.Keys.{artifactPath, libraryDependencies, mainClass, managedClasspath, name, organization, packageBin, resolvers, version}

logLevel := Level.Debug

name := "docker-nats-connector-spark-app"

organization := "logimethods"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.10.4"

val sparkVersion = "1.5.2"

libraryDependencies += "org.apache.spark" %% "spark-core" % sparkVersion % "provided"
/* http://stackoverflow.com/questions/31069136/sparkexception-local-class-incompatible */
///resolvers += "Talend" at "https://talend-update.talend.com/nexus/content/repositories/libraries/"
///libraryDependencies += "org.talend.libraries" % "spark-assembly-1.5.2-mapr-1602-hadoop2.7.0-mapr-1602" % "6.2.0"

//resolvers += Resolver.mavenLocal
resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
resolvers += "Sonatype OSS Release" at "https://oss.sonatype.org/content/groups/public/"

libraryDependencies += "org.apache.spark" %% "spark-streaming" % sparkVersion
libraryDependencies += "com.logimethods" % "nats-connector-spark" % "0.1.0-SNAPSHOT"
//libraryDependencies += "io.nats" % "jnats" % "0.4.1"
//libraryDependencies += "com.github.tyagihas" % "scala_nats_2.10" % "0.1"


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
    // @see https://mail-archives.apache.org/mod_mbox/spark-dev/201312.mbox/%3CCAPh_B=ass2NcrN41t7KTSoF1SFGce=N57YMVyukX4hPcO5YN2Q@mail.gmail.com%3E
    // @see http://apache-spark-user-list.1001560.n3.nabble.com/spark-1-6-Issue-td25893.html
    entryPoint("java", "-Xms128m", "-Xmx512m", "-XX:MaxPermSize=300m", "-cp", classpathString, mainclass)
  }
}