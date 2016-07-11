/*******************************************************************************
 * Copyright (c) 2016 Logimethods
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the MIT License (MIT)
 * which accompanies this distribution, and is available at
 * http://opensource.org/licenses/MIT
 *******************************************************************************/

package com.logimethods.nats.connector.spark.app

import java.util.Properties;

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.streaming.Duration
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.storage.StorageLevel;
import io.nats.client.Constants._

import com.logimethods.nats.connector.spark._

object SparkProcessor extends App {
  System.setProperty("org.slf4j.simpleLogger.log.org.apache.spark", "warn")
	System.setProperty("org.slf4j.simpleLogger.log.com.logimethods.nats.connector.spark", "trace")

	Thread.sleep(5000)

  val inputSubject = args(0)
  val outputSubject = args(1)
  println("Will process messages from " + inputSubject + " to " + outputSubject)

  val sparkMasterUrl = System.getenv("SPARK_MASTER_URL")
  println("SPARK_MASTER_URL = " + sparkMasterUrl)
  val conf = new SparkConf().setAppName("NATS Data Processing").setMaster(sparkMasterUrl);
  val sc = new SparkContext(conf);
//  sc.addJar("/app/nats-connector-spark-0.1.0-SNAPSHOT.jar:/app/jnats-0.3.1.jar")
  sc.addJar("/app/jnats-0.3.1.jar")
  sc.addJar("/app/nats-connector-spark-0.1.0-SNAPSHOT.jar")
  sc.addJar("/app/docker-nats-connector-spark-app_2.10-0.1.0.jar")
  val ssc = new StreamingContext(sc, new Duration(2000));

  val properties = new Properties();
  val natsUrl = System.getenv("NATS_URI")
  println("NATS_URI = " + natsUrl)
  properties.put("servers", natsUrl)
  properties.put(PROP_URL, natsUrl)
  val messages = ssc.receiverStream(NatsToSparkConnector.receiveFromNats(properties, StorageLevel.MEMORY_ONLY, inputSubject))

  // http://spark.apache.org/docs/latest/streaming-programming-guide.html#design-patterns-for-using-foreachrdd
/*  messages.foreachRDD { rdd =>
    val connectorPool = new SparkToNatsConnectorPool(properties, outputSubject)
    rdd.foreachPartition { partitionOfRecords =>
      val connector = connectorPool.getConnector()
      partitionOfRecords.foreach(record => connector.publishToNats(record))
      connectorPool.returnConnector(connector)  // return to the pool for future reuse
    }
  }*/
  
  val integers = messages.map({ str => Integer.parseInt(str) })
  val max = integers.reduce({ (int1, int2) => Math.max(int1, int2) })

  max.print()

  // http://spark.apache.org/docs/latest/streaming-programming-guide.html#design-patterns-for-using-foreachrdd
  max.foreachRDD { rdd =>
    val connectorPool = new SparkToNatsConnectorPool(properties, outputSubject)
    rdd.foreachPartition { partitionOfRecords =>
      val connector = connectorPool.getConnector()
      partitionOfRecords.foreach(record => connector.publishToNats(record))
      connectorPool.returnConnector(connector)  // return to the pool for future reuse
    }
  }
  
  ssc.start();		
  
  ssc.awaitTermination()
}