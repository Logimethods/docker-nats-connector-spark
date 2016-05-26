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
  properties.setProperty(io.nats.client.Constants.PROP_URL, "nats://nats-main:4222")
  val subject = System.getProperties().getProperty("GATLING.TO_NATS.SUBJECT", "fromGatling")
  println("Will emit messages to " + subject)
  val natsProtocol = NatsProtocol(properties, subject)
  
  val natsScn = scenario("NATS call").exec(NatsBuilder("Hello from Gatling!"))
 
  setUp(
    natsScn.inject(constantUsersPerSec(15) during (1 minute))
  ).protocols(natsProtocol)
}