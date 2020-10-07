package samples.streams;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;

import java.util.Arrays;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class WordCount {

    public static void main(String[] args) throws Exception {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-wordcount");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        final StreamsBuilder builder = new StreamsBuilder();

        // Create a Stream processor where we...
        // (1) split into words
        // (2) group by the words
        // (3) fetch the count and store into a `counts-store` KTable key->value (String, Long)
        // (4) spit to a stream
        KStream<String, String> source = builder.stream("streams-plaintext-input");
        source.flatMapValues(value -> Arrays.asList(value.toLowerCase(Locale.getDefault()).split("\\W+")))
                .groupBy((key, value) -> value)
                .count(Materialized.<String, Long, KeyValueStore<Bytes, byte[]>>as("counts-store"))
                .toStream()
                .to("streams-wordcount-output", Produced.with(Serdes.String(), Serdes.Long()));

        // Alternative way to write above ^ (with explicit mappers)
//        KTable<String, Long> counts =
//                source.flatMapValues(new ValueMapper<String, Iterable<String>>() {
//                    @Override
//                    public Iterable<String> apply(String str) {
//                        return Arrays.asList(str.toLowerCase(Locale.getDefault()).split("\\W+"));
//                    }
//                }).groupBy(new KeyValueMapper<String, String, String>() {
//                    @Override
//                    public String apply(String key, String value) {
//                        return value;
//                    }
//                }).count(Materialized.<String, Long, KeyValueStore<Bytes, byte[]>>as("counts-store"));
        // Materialize the result into a KeyValueStore named "counts-store".
        // The Materialized store is always of type <Bytes, byte[]> as this is the format of the inner most store.
        // This is what creates the topic `counts-store-repartition`

        // From here, we want to write `counts` KTable into another topic.
        // Counts above is a changelog stream, so we need to convert the types of changelogs to <String, Long>
//        counts.toStream().to("streams-wordcount-output", Produced.with(Serdes.String(), Serdes.Long()));

        final Topology topology = builder.build();
        System.out.println(topology.describe());

        /**
         *Topologies:
         *    Sub-topology: 0
         *     Source: KSTREAM-SOURCE-0000000000 (topics: [streams-plaintext-input])
         *       --> KSTREAM-FLATMAPVALUES-0000000001
         *     Processor: KSTREAM-FLATMAPVALUES-0000000001 (stores: [])
         *       --> KSTREAM-KEY-SELECT-0000000002
         *       <-- KSTREAM-SOURCE-0000000000
         *     Processor: KSTREAM-KEY-SELECT-0000000002 (stores: [])
         *       --> KSTREAM-FILTER-0000000005
         *       <-- KSTREAM-FLATMAPVALUES-0000000001
         *     Processor: KSTREAM-FILTER-0000000005 (stores: [])
         *       --> KSTREAM-SINK-0000000004
         *       <-- KSTREAM-KEY-SELECT-0000000002
         *     Sink: KSTREAM-SINK-0000000004 (topic: counts-store-repartition)
         *       <-- KSTREAM-FILTER-0000000005
         *
         *   Sub-topology: 1
         *     Source: KSTREAM-SOURCE-0000000006 (topics: [counts-store-repartition])
         *       --> KSTREAM-AGGREGATE-0000000003
         *     Processor: KSTREAM-AGGREGATE-0000000003 (stores: [counts-store])
         *       --> KTABLE-TOSTREAM-0000000007
         *       <-- KSTREAM-SOURCE-0000000006
         *     Processor: KTABLE-TOSTREAM-0000000007 (stores: [])
         *       --> KSTREAM-SINK-0000000008
         *       <-- KSTREAM-AGGREGATE-0000000003
         *     Sink: KSTREAM-SINK-0000000008 (topic: streams-wordcount-output)
         *       <-- KTABLE-TOSTREAM-0000000007
         *
         *  Here, we have two disconnected sub-topologies.
         *  The first subtopology has a sink-node (0004) which is the results of the repartition.
         *      The repartition "shuffles" the source by an aggregation key.
         *      FILTER (0005) is a stateless node between grouping and sink.
         *
         *  The second subtopology uses sink-node 0004 as a source to then write to a sink 0008.
         *      The aggregation node (0003) has a state store named Count-store.
         *      For each received record from source node, the aggregation processor will query current count for a key,
         *      augment it by one, then write new count back to the store.
         *      Each updated key will also be piped downstream (KTable-0007) and then pipe to KStream Sink 0008.
         */

        final KafkaStreams streams = new KafkaStreams(topology, props);
        final CountDownLatch latch = new CountDownLatch(1);

        // Same as Pipe...
        // attach shutdown handler to catch control-c
        Runtime.getRuntime().addShutdownHook(new Thread("streams-shutdown-hook") {
            @Override
            public void run() {
                streams.close();
                latch.countDown();
            }
        });

        try {
            streams.start();
            latch.await();
        } catch (Throwable e) {
            System.exit(1);
        }
        System.exit(0);
    }
}
