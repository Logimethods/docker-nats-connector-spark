FROM williamyeh/scala:2.11.6
ADD 0/scala-library-2.11.6.jar 1/scala_nats_2.10-0.1.jar 2/java_nats-0.5.1.jar /app/
ADD 3/nats-connector-spark_2.11-0.2.0-SNAPSHOT.jar /app/nats-connector-spark_2.11-0.2.0-SNAPSHOT.jar
ENTRYPOINT ["scala", "-cp", "\/app\/scala-library-2.11.6.jar:\/app\/scala_nats_2.10-0.1.jar:\/app\/java_nats-0.5.1.jar:\/app\/nats-connector-spark_2.11-0.2.0-SNAPSHOT.jar", "com.logimethods.nats.connector.spark.monitor.NatsOutputMonitor"]