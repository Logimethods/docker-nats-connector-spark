/*******************************************************************************
 * Copyright (c) 2016 Logimethods
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the MIT License (MIT)
 * which accompanies this distribution, and is available at
 * http://opensource.org/licenses/MIT
 *******************************************************************************/

package com.logimethods.nats.demo

import akka.actor.{ActorRef, Props}
import io.gatling.core.Predef._
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.config.{Protocol, Protocols}
import com.logimethods.nats.connector.gatling._

import scala.concurrent.duration._
import java.util.Properties
import io.nats.client.Constants.PROP_URL

class NatsInjection extends Simulation {
  
  val properties = new Properties()
  
  println("System properties: " + System.getenv())
  
  // The URI of the NATS server is provided by an environment variable:
  // >export NATS_URI=nats://nats-main:4222
  val natsUrl = System.getenv("NATS_URI")
  properties.setProperty(io.nats.client.Constants.PROP_URL, natsUrl)
  
  // The NATS Subject is also provided by an environment variable:
  // >export GATLING.TO_NATS.SUBJECT=FROM_GATLING  
  var subject = System.getenv("GATLING.TO_NATS.SUBJECT")
  
  if (subject == null) {
    println("No Subject has been defined through the 'GATLING.TO_NATS.SUBJECT' Environment Variable!!!")
  } else {
    println("Will emit messages to " + subject)
    val natsProtocol = NatsProtocol(properties, subject)
    
    // The messages sent to NATS will not be fixed thanks to the ValueProvider.
    val natsScn = scenario("NATS call").exec(NatsBuilder(new ValueProvider()))
   
    setUp(
      natsScn.inject(constantUsersPerSec(15) during (1 minute))
    ).protocols(natsProtocol)
  }
}

/**
 * The ValueProvider will generate a loop of values: 100, 110, 120, 130, 140, 150, 100...
 */
class ValueProvider {
  val incr = 10
  val basedValue = 100 -incr
  val maxIncr = 50
  var actualIncr = 0
  
  override def toString(): String = {
    actualIncr = (actualIncr % (maxIncr + incr)) + incr
    (basedValue + actualIncr).toString()
  }
}