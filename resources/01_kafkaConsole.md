# Kafka Console commands

From Quickstart guide (https://kafka.apache.org/quickstart).

- Download tarball from Kafka website
- Command to start Zookeeper
```
bin/zookeeper-server-start.sh config/zookeeper.properties
```

- Command to start one Kafka broker
```
bin/kafka-server-start.sh config/server.properties
```

- Command to start one Kafka producer
```
bin/kafka-console-producer.sh --topic quickstart-events --bootstrap-server localhost:9092
```
Creates an interface to accept messages from console input.

- Command to start one Kafka consumer
```
bin/kafka-console-consumer.sh --topic quickstart-events --from-beginning --bootstrap-server localhost:9092
```
Consumes messages and outputs to console.
