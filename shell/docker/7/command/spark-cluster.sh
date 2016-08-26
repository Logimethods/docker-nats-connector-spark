#!/bin/bash
# Spark Shell with libs
SPARK_JAVA_OPTS+=' -Dspark.serializer=org.apache.spark.serializer.KryoSerializer -Dspark.kryo.registrator=org.apache.spark.graphx.GraphKryoRegistrator '
export SPARK_JAVA_OPTS
spark-shell --conf spark.streaming.stopGracefullyOnShutdown=true --jars $(echo ./lib_add/*.jar | tr ' ' ','),$(echo ./lib_main/*.jar | tr ' ' ',') --master ${SPARK_MASTER_URL}
