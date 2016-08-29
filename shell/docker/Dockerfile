FROM gettyimages/spark:1.6.1-hadoop-2.6
ADD 0/scala-library.jar 1/nats-connector-spark-0.1.0.jar 2/jnats-0.3.1.jar 3/slf4j-api-1.7.5.jar 4/slf4j-simple-1.7.5.jar ./lib_add/
ADD 5/docker-nats-connector-spark_2.10-0.1.0.jar ./lib_main/docker-nats-connector-spark_2.10-0.1.0.jar
ADD 6/script ./script
ADD 7/command ./
ENV SPARK_LIBS_PATHS="./lib_add/scala-library.jar:./lib_add/nats-connector-spark-0.1.0.jar:./lib_add/jnats-0.3.1.jar:./lib_add/slf4j-api-1.7.5.jar:./lib_add/slf4j-simple-1.7.5.jar:./lib_main/docker-nats-connector-spark_2.10-0.1.0.jar"
CMD ["sleep", "infinity"]