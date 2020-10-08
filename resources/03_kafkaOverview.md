#Overview of Kafka

## Is Kafka a database? Does it satisfy ACID?
 - <https://www.youtube.com/watch?v=v2RJQELoM6Y&feature=emb_logo>
 - <https://dzone.com/articles/is-apache-kafka-a-database-the-2020-update#:~:text=Apache%20Kafka%20is%20a%20database.&text=However%2C%20in%20many%20cases%2C%20Kafka,downtime%20and%20zero%20data%20loss>

## Duality of Streams and Tables

When implementing stream processing in practice, we typically need BOTH streams and databases. This is called *stream-table duality*.

- On one hand, we can turn a stream into a table by aggregations. So we can recreate the latest chess board state by replaying all recorded moves from the stream.
- On the other hand, we can turn a table into a stream by capturing all changes made to the table (like a changelog).

## If streams can hold indefinite amount of logs, how does it not run out of memory?

[Log Compaction](https://kafka.apache.org/26/documentation/#compaction). To ensure Kafka doesn't run out of memory, when we purge old data, we retain the last known value for each message key and pretend it's the "first" message of the current stream.



## How does Kafka guarantee in-order, exactly once processing (idempotency and atomicity)

https://www.confluent.io/blog/exactly-once-semantics-are-possible-heres-how-apache-kafka-does-it/

## Advantages vs. Disadvantages of Kafka

https://www.javatpoint.com/apache-kafka-advantages-and-disadvantages

**Advantages**

- Low Latency, High throughput!
- Fault Tolerance (easy to restart after machine failure)
- Durability (Replication)
- Easily accessible
- Scalable! Just make more brokers, producers, and consumers

**Disadvantages**

- Monitoring tools not "complete". Still in the works, so some people fear working with Kafka.
- Can't mutate messages. Once in, they cannot be changed.
- Still possible to lose some messages if you're only using Kafka (log compression). One major reason why you still need other databases to complement Kafka.

## Similar to Kafka
- Pulsar: https://www.confluent.io/kafka-vs-pulsar/. Basically Kafka but with namespaced streams and ACL support.
- RabbitMQ: https://www.cloudamqp.com/blog/2019-12-12-when-to-use-rabbitmq-or-apache-kafka.html
