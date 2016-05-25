/*******************************************************************************
 * Copyright (c) 2016 Logimethods
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the MIT License (MIT)
 * which accompanies this distribution, and is available at
 * http://opensource.org/licenses/MIT
 *******************************************************************************/

package com.logimethods.nats.connector.spark.app

import java.util.Properties
import org.nats._

// @see https://github.com/tyagihas/scala_nats
object DummyNatsReceiver extends App {
  Thread.sleep(2000)
  
  val properties = new Properties()
  properties.setProperty(/*io.nats.client.Constants.PROP_URL*/ "io.nats.client.url", "nats://nats-main:4222")
  var conn = Conn.connect(properties)

  conn.subscribe(args(0), (msg:Msg) => {println("Received update : " + msg.body)})
}