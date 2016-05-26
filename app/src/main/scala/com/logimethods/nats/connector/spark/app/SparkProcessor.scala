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

import com.logimethods.nats.connector.spark.NatsToSparkConnector

object SparkProcessor extends App {
		val conf = new SparkConf().setAppName("NATS Data Processing").setMaster("local[2]");
		val sc = new SparkContext(conf);
		val ssc = new StreamingContext(sc, new Duration(200));
		
		val properties = new Properties();
		val messages = ssc.receiverStream(NatsToSparkConnector.receiveFromNats(properties, StorageLevel.MEMORY_ONLY, "INPUT"))
		
		val integers = messages.map({ str => Integer.parseInt(str) })
		val max = integers.reduce({ (int1, int2) => Integer.max(int1, int2) })
		
		max.print()
}