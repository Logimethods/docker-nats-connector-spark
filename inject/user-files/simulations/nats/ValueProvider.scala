/*******************************************************************************
 * Copyright (c) 2016 Logimethods
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the MIT License (MIT)
 * which accompanies this distribution, and is available at
 * http://opensource.org/licenses/MIT
 *******************************************************************************/

package com.logimethods.nats.demo

import com.logimethods.connector.gatling.to_nats.NatsMessage
import java.nio.ByteBuffer

class ValueProvider(subject: String) extends NatsMessage {
  val incr = 10
  val basedValue = 100 -incr
  val maxIncr = 50
  var actualIncr = 0
  
  def getPayload(): Array[Byte] = {
    actualIncr = (actualIncr % (maxIncr + incr)) + incr
    // https://docs.oracle.com/javase/8/docs/api/java/nio/ByteBuffer.html
    val buffer = ByteBuffer.allocate(4);
    buffer.putFloat((basedValue + actualIncr))
    
    return buffer.array()
  }
}