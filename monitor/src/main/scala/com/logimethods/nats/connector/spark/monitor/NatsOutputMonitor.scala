/*******************************************************************************
 * Copyright (c) 2016 Logimethods
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the MIT License (MIT)
 * which accompanies this distribution, and is available at
 * http://opensource.org/licenses/MIT
 *******************************************************************************/

package com.logimethods.nats.connector.spark.monitor

import java.util.Properties
import org.nats._

// @see https://github.com/tyagihas/scala_nats
object NatsOutputMonitor extends App {
  Thread.sleep(2000)
  
  val properties = new Properties()
  //@see https://github.com/tyagihas/java_nats/blob/master/src/main/java/org/nats/Connection.java
  properties.put("servers", "nats://nats-main:4222")
  val conn = Conn.connect(properties)

  val inputSubject = args(0)
  println("Will be listening to messages from " + inputSubject)

  if (args.length > 1) {// TEST mode
    val espectedValue = args(1)
    println("Is especting a value equals to " + espectedValue)
    
    var iterations = 3
    conn.subscribe(inputSubject, (msg: Msg) => {
      println("Received message: " + msg.body)
      iterations -= 1
      if (iterations <= 0) {
        val receivedValue = msg.body.toInt
        if (receivedValue == espectedValue) { // "Tests passed!"
          System.exit(0)
        } else { // "Tests failed!"
          System.exit(1)
        }
      }
    })          
  } else { // REGULAR mode
    conn.subscribe(inputSubject, (msg: Msg) => {
      println("Received message: " + msg.body)
    })    
  }
}