package services.streams;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.processor.RecordContext;
import org.apache.kafka.streams.processor.StreamPartitioner;
import org.apache.kafka.streams.processor.TopicNameExtractor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import services.common.KafkaUtils;
import services.common.Topics;
import services.entities.Match;
import services.entities.User;
import services.serializers.MatchSerde;
import services.serializers.UserDeserializer;
import services.serializers.UserSerde;
import services.serializers.UserSerializer;
import services.stores.UserStore;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class MatchToUserStream {
    private static final Logger LOGGER = LogManager.getLogger(MatchToUserStream.class);
    private final String APPLICATION_ID = "streams-match-2-user";
    private Topology topology;
    private KafkaStreams streams;

    public MatchToUserStream() {
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
        return props;
    }

    private Topology createTopology() {
        final StreamsBuilder builder = new StreamsBuilder();
        KStream<String, Match> sourceStreamMatches = builder.stream(Topics.MATCH_INPUT,
                Consumed.with(Serdes.String(), new MatchSerde()));

        sourceStreamMatches
                .filter((matchId, match) -> match.endedAt == null)
                .map((matchId, match) -> {
                    LOGGER.debug("ProxyingA... " + matchId + " " + match.endedAt);
                    return KeyValue.pair(matchId, match);
                })
                .flatMap((matchId, match) -> {
                    List<Long> userIds = match.userIds;
                    List<User> updatedUsers = userIds.stream()
                            .map(userId -> UserStore.getUser(userId))
                            .filter(user -> user != null)
                            .map(user -> {
                                user.queueState = User.QUEUE_STATE_MATCHED;
                                user.queueStateUpdatedAt = Date.from(Instant.now());
                                return user;
                            }).collect(Collectors.toList());
                    return updatedUsers.stream().map(user -> KeyValue.pair(user.id, user))
                            .collect(Collectors.toList());
                })
                .to(Topics.USER_OUTPUT, Produced.with(Serdes.Long(), new UserSerde()));

        sourceStreamMatches
                .filter((matchId, match) -> match.endedAt != null)
                .map((matchId, match) -> {
                    LOGGER.debug("ProxyingB... " + matchId + " " + match.endedAt);
                    return KeyValue.pair(matchId, match);
                })
                .flatMap((matchId, match) -> {
                    List<Long> userIds = match.userIds;
                    List<User> updatedUsers = userIds.stream()
                            .map(userId -> UserStore.getUser(userId))
                            .filter(user -> user != null)
                            .map(user -> {
                                user.queueState = User.QUEUE_STATE_NORMAL;
                                user.queueStateUpdatedAt = Date.from(Instant.now());
                                return user;
                            }).collect(Collectors.toList());
                    return updatedUsers.stream().map(user -> KeyValue.pair(user.id, user))
                            .collect(Collectors.toList());
                })
                .to(Topics.USER_OUTPUT, Produced.with(Serdes.Long(), new UserSerde()));
        return builder.build();
    }
}
