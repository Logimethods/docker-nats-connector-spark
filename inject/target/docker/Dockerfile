FROM denvazh/gatling:2.1.7
ADD 0/scala-library-2.11.8.jar 1/nats-connector-gatling_2.11.jar 2/jnats-0.4.1.jar 3/slf4j-api-1.7.5.jar ./lib/
ADD 4/user-files ./user-files
ADD 5/scripts ./bin
CMD ["-s com.logimethods.nats.demo.NatsInjection"]