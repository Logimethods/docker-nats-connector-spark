#!/bin/bash
# Spark Shell with libs
SPARK_JAVA_OPTS+=' -Dspark.serializer=org.apache.spark.serializer.KryoSerializer -Dspark.kryo.registrator=org.apache.spark.graphx.GraphKryoRegistrator '
export SPARK_JAVA_OPTS
spark-shell --conf spark.streaming.stopGracefullyOnShutdown=true --jars $(echo ./target/dependencies/*.jar | tr ' ' ','),$(echo ./target/*.jar | tr ' ' ',') --master spark://master:7077
#,$(echo ./libs/*.jar | tr ' ' ',')
