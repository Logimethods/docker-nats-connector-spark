/*******************************************************************************
 * Copyright (c) 2016 Logimethods
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the MIT License (MIT)
 * which accompanies this distribution, and is available at
 * http://opensource.org/licenses/MIT
 *******************************************************************************/

package com.logimethods.nats.connector.spark.app

import java.util.Properties;
import java.io.File

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.streaming.Duration
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.storage.StorageLevel;
import io.nats.client.Constants._

import org.apache.log4j.{Level, LogManager, PropertyConfigurator}

import com.logimethods.connector.nats.to_spark._
import com.logimethods.scala.connector.spark.to_nats._

import org.apache.hadoop.security.UserGroupInformation

object SparkProcessor extends App {

  // ALL, DEBUG, ERROR, FATAL, INFO, OFF, TRACE, TRACE_INT, WARN
  val logLevel = Level.toLevel(scala.util.Properties.envOrElse("LOG_LEVEL", "INFO"))
  println("LOG_LEVEL = " + logLevel)
  
  val log = LogManager.getRootLogger
  log.setLevel(logLevel)

  Thread.sleep(5000)

  val inputSubject = args(0)
  val inputStreaming = inputSubject.toUpperCase.contains("STREAMING")
  val outputSubject = args(1)
  val outputStreaming = outputSubject.toUpperCase.contains("STREAMING")
  println("Will process messages from " + inputSubject + " to " + outputSubject)

  val sparkMasterUrl = System.getenv("SPARK_MASTER_URL")
  println("SPARK_MASTER_URL = " + sparkMasterUrl)
  val conf = new SparkConf().setAppName("NATS Data Processing").setMaster(sparkMasterUrl);

  // https://stackoverflow.com/questions/41864985/hadoop-ioexception-failure-to-login
  UserGroupInformation.setLoginUser(UserGroupInformation.createRemoteUser("sparkuser"));

  val sc = new SparkContext(conf);
//  val jarFilesRegex = "java-nats-streaming-(.*)jar|guava(.*)jar|protobuf-java(.*)jar|jnats-(.*)jar|nats-connector-spark-(.*)jar|docker-nats-connector-spark(.*)jar"
  val jarFilesRegex = "(.*)jar"
  for (file <- new File("/app/").listFiles.filter(_.getName.matches(jarFilesRegex)))
    { sc.addJar(file.getAbsolutePath) }
  val ssc = new StreamingContext(sc, new Duration(2000));

  val properties = new Properties();
  val natsUrl = System.getenv("NATS_URI")
  println("NATS_URI = " + natsUrl)
  properties.put("servers", natsUrl)
  properties.put(PROP_URL, natsUrl)

  val clusterId = System.getenv("NATS_CLUSTER_ID")

  val integers =
    if (inputStreaming) {
      NatsToSparkConnector
        .receiveFromNatsStreaming(classOf[Integer], StorageLevel.MEMORY_ONLY, clusterId)
        .withNatsURL(natsUrl)
        .withSubjects(inputSubject)
        .asStreamOf(ssc)
    } else {
      NatsToSparkConnector
        .receiveFromNats(classOf[Integer], StorageLevel.MEMORY_ONLY)
        .withProperties(properties)
        .withSubjects(inputSubject)
        .asStreamOf(ssc)
    }

  val max = integers.reduce({ (int1, int2) => Math.max(int1, int2) })

  if (logLevel.isGreaterOrEqual(Level.INFO)) {
    println("Will print all MAX values")
    max.print()
  }

  if (outputStreaming) {
    SparkToNatsConnectorPool.newStreamingPool(clusterId)
                            .withNatsURL(natsUrl)
                            .withSubjects(outputSubject)
                            .publishToNats(max)
  } else {
    SparkToNatsConnectorPool.newPool()
                            .withProperties(properties)
                            .withSubjects(outputSubject)
                            .publishToNats(max)
  }

  ssc.start();

  ssc.awaitTermination()
}