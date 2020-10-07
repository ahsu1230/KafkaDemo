package samples.streams;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;

/**
 * This is an introduction to the Kafka STREAM API which is used for clients to consume data.
 * With the Kafka Stream API,
 * you can create custom "flows" of how data flows between streams / topics.
 * You can follow these flows by looking at the Topology.
 */
public class Pipe {

    public static void main(String[] args) throws Exception {
        Properties props = new Properties();

        // Unique ID to differentiate application in Kafka cluster.
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-pipe");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        final StreamsBuilder builder = new StreamsBuilder();

        // THIS IS OUR "SOURCE STREAM" to "DESTINATION STREAM"
        // These are both topic names
        builder.stream("streams-plaintext-input").to("streams-pipe-output");

        final Topology topology = builder.build();
        System.out.println(topology.describe());
        // Output looks like this:
        /**
         * Topologies:
         *    Sub-topology: 0
         *     Source: KSTREAM-SOURCE-0000000000 (topics: [streams-plaintext-input])
         *       --> KSTREAM-SINK-0000000001
         *     Sink: KSTREAM-SINK-0000000001 (topic: streams-pipe-output)
         *       <-- KSTREAM-SOURCE-0000000000
         */

        // This stream simply "pipes" data from one Kafka topic to another
        final KafkaStreams streams = new KafkaStreams(topology, props);

        // Countdown latching is for adding a shutdown hook
        final CountDownLatch latch = new CountDownLatch(1);
        // attach shutdown handler to catch control-c
        Runtime.getRuntime().addShutdownHook(new Thread("streams-shutdown-hook") {
            @Override
            public void run() {
                streams.close();
                latch.countDown();
            }
        });

        try {
            streams.start();        // Execute client
            latch.await();
        } catch (Throwable e) {
            System.exit(1);
        }
        System.exit(0);
    }
}
