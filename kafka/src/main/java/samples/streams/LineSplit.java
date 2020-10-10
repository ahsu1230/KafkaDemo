package samples.streams;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;

import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class LineSplit {

    public static void main(String[] args) throws Exception {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-linesplit");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        final StreamsBuilder builder = new StreamsBuilder();

        // The input stream comes as a String key-value pair
        // For every value in this stream of pairs, split a string into words (split by spaces)
        // So each string could have multiple words and dump into output stream
        KStream<String, String> source = builder.stream("streams-plaintext-input");
        source.flatMapValues(value -> Arrays.asList(value.split("\\W+")))
                .to("streams-linesplit-output");

        final Topology topology = builder.build();
        System.out.println(topology.describe());
        /**
         * Topology groph is the children/parent relationships of streams.
         * In this case, we see that a processor (FLATMAP) sits between a source and sink.
         * Note that the processor node is stateless.
         *
         * Topologies:
         *    Sub-topology: 0
         *     Source: KSTREAM-SOURCE-0000000000 (topics: [streams-plaintext-input])
         *       --> KSTREAM-FLATMAPVALUES-0000000001
         *     Processor: KSTREAM-FLATMAPVALUES-0000000001 (stores: [])
         *       --> KSTREAM-SINK-0000000002
         *       <-- KSTREAM-SOURCE-0000000000
         *     Sink: KSTREAM-SINK-0000000002 (topic: streams-linesplit-output)
         *       <-- KSTREAM-FLATMAPVALUES-0000000001
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
