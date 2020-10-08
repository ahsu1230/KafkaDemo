# Kafka Streams

***Resources:***

- <https://kafka.apache.org/26/documentation/streams/core-concepts>
- <https://kafka.apache.org/26/documentation/streams/architecture>

- Kafka Streams is a client library for processing data stored in Kafka through stream processing concepts (event-time, processing-time, sliding window, real-time querying, etc.)
- Can write data processors on a single machine, and scale by running processor across additional instances over multiple machines. Kafka Streams handles the load balancing for you.
- Easy to embed into any Java application.
- No dependencies other than Kafka itself.
- Guarantees *exactly-once* processing, employs *one-record-at-a-time* processing.

## Stream Processing Topology

- A *stream* represents an unbounded, continuously updating data set. It is ordered, replayable, and fault-tolerant (local-state) sequence of immutable data-records. Every data record is a key-value pair.
- A *stream processing application* is made from a graph of computations called *processor topologies*. Nodes are stream processors, edges are streams.
- A *stream processor* represents the processing step to transform data records from one stream to another stream. "Upstream processors" are the parents while the "downstream processors" are the children. We flow from top to bottom.
- *Source Processor* is a "starting point" - a type of processor with no upstream. It consumes records from a Kafka topic and forwards messages to down-stream processors.
- *Sink Processor* is an "end point" - a type of processor with no downstream. It only receives records from up-stream processors and sends "final results" to a Kafka topic.

## Kafka Streams DSL

The [Domain Specific Language (DSL)](https://kafka.apache.org/26/documentation/streams/developer-guide/dsl-api.html) is Kafka's API of common data transformation operations (i.e. `map`, `filter`, `join`, etc.). [Processor API](https://kafka.apache.org/26/documentation/streams/developer-guide/processor-api.html) allows you to define your own custom processors.

## Time in Kafka

All records are automatically timestamped for you (`Event time`). Unlike other DBs where you would have to include a `created_at` column / property for each record.

Event time vs. Processing time. A single record can go through multiple "states". It first gets created "at the source", but as it sits in a topic, it remains `UNPROCESSED` until it is its turn to be processed by a processor application. Once it's consumed, it is PROCESSED.

Ingestion time. A record is "ingested" when it is stored into a topic partition.
So I think the order usually goes Event -> Ingestion -> Processed?

Be careful with timestamps! Their value heavily depends on the context.
- Does the record come from a processor?
- When aggregated or joins, the timestamp is the maximum timestamp of all resulting records.

## KStream vs. KTable

<https://www.confluent.io/blog/kafka-streams-tables-part-1-event-streaming/>

- Streams record history while Tables represent state of the world. Think like list of chess moves by both players vs. birds-eye view of the board.
- A Stream contains IMMUTABLE data. It only supports appends whereas existing events cannot be changed. Stream events can be identified by a key.
- A Table provides MUTABLE data. Rows can be identified by key and mutated.

## States

Sometimes you need a stream processing application that is dependent of processing of other messages (like count, max, groupby, etc.). Kafka Streams provide **state stores** which are key-value stores (persistent, in-memory, up to you). Kafka Streams fault tolerance and recovery for local state stores.

**How does Kafka guarantee fault tolerance?**

- If a task running on a machine fails, Kafka Streams automatically restarts the task in other running instances.
- For state stores, it maintains a replicated changelog Kafka topic to track state updates. These changelog topics are partitioned as well so each local state store has multiple partitions as backup so you can replay actions to bring the state back up to speed after a failure.
- Old data can be purged safely to prevent topics from growing indefinitely.

## Paralellism

https://kafka.apache.org/26/documentation/streams/architecture

- A single topic may consist of many partitions. Usually these partitions live in different brokers (servers), but they all map to the same topic.
- A stream partition is an ordered sequence of records as part of a topic partition.
- The key of a record determines which partition of a topic
- Usually partitions are not dependent to each other so each partition can be handled by a single thread and no need for inter-thread coordination. Done for you by Kafka Streams.
