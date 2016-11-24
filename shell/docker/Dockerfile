FROM gettyimages/spark:1.6.1-hadoop-2.6
ADD 0/scala-library.jar 1/nats-connector-spark-0.3.0.jar ./lib_add/
ADD 2/docker-nats-connector-spark_2.10-0.3.0.jar ./lib_main/docker-nats-connector-spark_2.10-0.3.0.jar
ADD 3/script ./script
ADD 4/command ./
ENV SPARK_LIBS_PATHS="./lib_add/scala-library.jar:./lib_add/nats-connector-spark-0.3.0.jar:./lib_main/docker-nats-connector-spark_2.10-0.3.0.jar"
CMD ["sleep", "infinity"]