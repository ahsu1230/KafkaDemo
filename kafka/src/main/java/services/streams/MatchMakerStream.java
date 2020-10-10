package services.streams;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import services.common.KafkaUtils;
import services.common.Topics;
import services.entities.User;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class MatchMakerStream {

    private static Topology createTopology() {
        final StreamsBuilder builder = new StreamsBuilder();

        // The input stream comes as a key-value pair <userId, User>
        // For every pair...
        // We want the first 4 users who are QUEUED, online and have the same level
        KStream<Long, User> sourceStreamUser = builder.stream(Topics.USER);
//        KTable<Integer, List<User>> tableByLevel = sourceStreamUser
//                .filter((userId, user) -> user.isOnline && User.QUEUE_STATE_QUEUED.equals(user.queueState))
//                .groupBy((userId, user) -> user.level)
//                .aggregate(
//                        () -> new ArrayList<User>(),
//                        (level, user, userList) -> {
//
//                        },
//                        Materialized.as("aggr-level-users").withValueSerde(Serdes.Integer()));


//        source.flatMapValues(value -> Arrays.asList(value.split("\\W+")))
//                .to("streams-linesplit-output");

        return builder.build();
    }


    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "mmstreams-matchmake");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaUtils.BOOTSTRAP_ADDRESS_LOCAL);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        final Topology topology = createTopology();
        final KafkaStreams streams = new KafkaStreams(topology, props);
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
            streams.start();
            latch.await();
        } catch (Throwable e) {
            System.exit(1);
        }
        System.exit(0);
    }
}
