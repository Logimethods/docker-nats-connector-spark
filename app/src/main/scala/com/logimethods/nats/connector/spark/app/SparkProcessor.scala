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

	Thread.sleep(3000)

  val inputSubject = args(0)
  val outputSubject = args(1)
  println("Will process messages from " + inputSubject + " to " + outputSubject)

  val conf = new SparkConf().setAppName("NATS Data Processing").setMaster("local[2]");
  val sc = new SparkContext(conf);
  val ssc = new StreamingContext(sc, new Duration(2000));

  val properties = new Properties();
  properties.put("servers", "nats://nats-main:4222")
  properties.put(PROP_URL, "nats://nats-main:4222")
  val messages = ssc.receiverStream(NatsToSparkConnector.receiveFromNats(properties, StorageLevel.MEMORY_ONLY, "INPUT"))

  val integers = messages.map({ str => Integer.parseInt(str) })
  val max = integers.reduce({ (int1, int2) => Math.max(int1, int2) })

  max.print()
  
  max.map { m => SparkToNatsConnector.publishToNats(properties, "OUTPUT").call(m.toString()) }
  
//  val maxStr = max.map { m => m.toString() }
//  maxStr.map(SparkToNatsConnector.publishToNats(properties, "OUTPUT"))
  
  ssc.start();		
  
  ssc.awaitTermination()
}