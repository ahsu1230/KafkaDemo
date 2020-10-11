package services.streams;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import services.common.KafkaUtils;
import services.common.Topics;
import services.entities.Match;
import services.entities.User;
import services.serializers.MatchSerde;
import services.stores.UserStore;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class MatchToMatchStream {
    private static final Logger LOGGER = LogManager.getLogger(MatchToMatchStream.class);
    private final String APPLICATION_ID = "streams-match-2-match";
    private Topology topology;
    private KafkaStreams streams;

    public MatchToMatchStream() {
        Properties props = configureProperties();
        this.topology = createTopology();
        this.streams = new KafkaStreams(topology, props);
    }

    public void start() {
        streams.start();
    }

    public void close() {
        streams.close();
    }

    private Properties configureProperties() {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, APPLICATION_ID);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaUtils.BOOTSTRAP_ADDRESS_LOCAL);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, MatchSerde.class);
        return props;
    }

    private Topology createTopology() {
        final StreamsBuilder builder = new StreamsBuilder();
        KStream<String, Match> sourceStreamMatches = builder.stream(Topics.MATCH_INPUT);

        sourceStreamMatches
                .filter((matchId, match) -> match.endedAt == null)
                .map((matchId, match) -> {
                    LOGGER.debug("Proxying1... " + matchId);
                    return KeyValue.pair(matchId, match);
                })
                .to(Topics.MATCH_OUTPUT);
        return builder.build();
    }
}
