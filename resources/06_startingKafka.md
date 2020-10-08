# Starting Kafka

- Run Kafka locally <https://docs.confluent.io/current/quickstart/ce-quickstart.html>
- Run Kafka locally via Docker <https://docs.confluent.io/current/quickstart/ce-docker-quickstart.html>

## What is Confluent Platform?

Event Streaming Management platform. Start Kafka clusters and let Confluent Platform manage everything, including Connectors (external dbs as sink/sources), logs, etc.

https://docs.confluent.io/current/platform.html


# Running Kafka locally via Docker

- Follow guide linked above
- Run `docker-compose up -d` at correct directory
- Use [Confluent Control Center](https://docs.confluent.io/current/control-center/index.html#control-center) to build and monitor data pipelines and event streaming apps.
- Wait a bit and go to `http://localhost:9021` to go to local Confluent Control Center website. Double check with `docker-compose ps` if that is the correct url/port.
- When it is up, select CO Cluster1, and add topics!
- ... stopped... can't continue because `connect` container keeps failing.

# Running Kafka locally

- Follow guide linked above
- Download zip and unzip
- Configure $PATH and $CONFLUENT_HOME
- Install Kafka Connect Datagen (?) - optional. Don't need to do this if you're creating your own sample data.
- Run `confluent local services start` to start all services!
  - The services that should be running are:
  - Zookeeper, Kafka, Schema Registry, Kafka REST, Connect, KSQL Server, Control Center
