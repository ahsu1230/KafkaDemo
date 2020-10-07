# Introduction to Kafka

https://kafka.apache.org/intro

Most databases are good for managing data about objects / entities.
- Kafka is better at managing data about “events”
- Think like logs, streams of data, etc.
- Every record in Kafka is about an event that happened at some time.

## 3 Major principles around Kafka

- Publishing (writing) and subscribing (reading) to streams of events
- Storing streams of events durably & reliably (highly scalable, secure, elastic, distributed, fault tolerant)
- Can be deployed in any environment.

## Requirements

- Kafka requires Java8
- Runs on top of Apache Zookeeper (Apache service for centrally managing distributed systems). It is a distributed key-value store used for service discovery
  - For reference, Kubernetes also does service-discovery, but is more of an orchestration tool - including deployment, discovery, and “self-healing” of services.

---

## Key Terminologies

### Records or Messages
- A single data entry about an event
- Many records live in a partition. And consumers consume records one at a time. They keep track of which record they’ve seen and what is next using the Offsets.
- Usually has a key, value, and timestamp.

### Producers and Consumers
- Producers - client applications that publish (write) events to Kafka
- Consumers - client applications that subscribe and read events
- In kafka, producers and consumers are agnostic of each other - meaning they are totally separate and don’t depend on each other. This is how Kafka achieves high scalability. You can have LOTS of consumers and/or LOTS of producers. Producers never wait on consumers and vice-versa.

### Topics
- A topic is a “folder” of events (i.e. payments). A single topic can have multiple producers and multiple consumers.
- Records are NOT deleted after consumption. Records are retained by time. Too old records get discarded. Can be configured per topic.
- Kafka performance is not dependent on data size, so no problem storing LOTS of data in partition.

### Partitions
- Distributions of topics. A topic can be distributed into buckets across different Kafka brokers (servers). So “payments” topic can be split into 4 partitions across 4 brokers (P1, P2, P3, P4). How they are split are based on the event key (user, customer, vehicleId).
- When an event is written, the event appends to a partition -> which can then be read by multiple consumers.
- Furthermore, topics are replicated so multiple brokers can have copies of the overall data - configured by a setting.
  - We often call these LEADER partitions or REPLICA partitions.

### Brokers
- A type of server within a Kafka cluster. These servers act as storage layers.
- A broker can contain multiple partitions.

### Offsets
- The “position” (index) of a certain consumer in a partition. This is how a consumer knows what to consume next.

### Kafka Connect (Import/Export)
- Another type of server in a Kafka cluster. These servers continuously import/export data as event streams into / out of Kafka clusters.

### Kafka Stream (Client Consumption)
- API library for clients to consume data from Kafka cluster. Available in Java, Scala.
