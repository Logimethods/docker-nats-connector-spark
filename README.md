# docker-nats-connector-spark
A collection of Docker Images to illustrate the use of the [nats-connector-spark](https://github.com/Logimethods/nats-connector-spark) & [nats-connector-gatling](https://github.com/Logimethods/nats-connector-gatling) librairies.

## Usage
    cd compose
    docker-compose up

## How it works...
1) Gatling does emit every 15 sec a stream of values from 100 to 150 into NATS. See [NatsInjection.scala](https://github.com/Logimethods/docker-nats-connector-spark/blob/master/inject/user-files/simulations/nats/NatsInjection.scala).

2) Those values are collected from NATS to Spark Streaming, then the maximum value of each stream is reemited into NATS. See [SparkProcessor.scala](https://github.com/Logimethods/docker-nats-connector-spark/blob/master/app/src/main/scala/com/logimethods/nats/connector/spark/app/SparkProcessor.scala)

3) Finally, those values are monitored and printed into the console. See [NatsOutputMonitor.scala](https://github.com/Logimethods/docker-nats-connector-spark/blob/master/monitor/src/main/scala/com/logimethods/nats/connector/spark/monitor/NatsOutputMonitor.scala)

## Build

Those Docker Images are pushed to [dockerhub:lmagnin/docker-nats-connector-spark](https://hub.docker.com/r/lmagnin/docker-nats-connector-spark/) and build there.

![build.png](build.png "Global Build")
    
## Links
* [nats-connector-gatling on Github](https://github.com/Logimethods/nats-connector-gatling)
* [nats-connector-spark on Github](https://github.com/Logimethods/nats-connector-spark)
* [docker-nats-connector-spark on Github](https://github.com/Logimethods/docker-nats-connector-spark)
* [nats-connector-gatling build on Wercker](https://app.wercker.com/logimethods/nats-connector-gatling)
* [nats-connector-spark build on Wercker](https://app.wercker.com/logimethods/nats-connector-spark)
* [logimethods on Nexus](https://oss.sonatype.org/#nexus-search;quick~logimethods)
* [docker-nats-connector-spark build on Wercker](https://app.wercker.com/logimethods/docker-nats-connector-spark)
* [nats-connector-spark on Docker Hub](https://hub.docker.com/r/logimethods/nats-connector-spark/)
