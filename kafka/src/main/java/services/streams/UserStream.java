package services.streams;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import services.common.KafkaUtils;
import services.common.Topics;
import services.entities.Match;
import services.entities.User;
import services.entities.UserAggregator;
import services.serializers.MatchSerde;
import services.serializers.UserSerde;

import java.util.*;
import java.util.stream.Collectors;

public class UserStream {
    private static final Logger LOGGER = LogManager.getLogger(UserStream.class);
    private final String APPLICATION_ID = "streams-user";
    private Topology topology;
    private KafkaStreams streams;

    public UserStream() {
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
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.Long().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, UserSerde.class);
        return props;
    }

    private Topology createTopology() {
        final StreamsBuilder builder = new StreamsBuilder();

        KStream<Long, User> sourceStreamUsers = builder.stream(Topics.USER_INPUT);
        sourceStreamUsers
                .map((userId, user) -> {
                    LOGGER.debug("Proxying... " + userId + ": " + user.username);
                    return KeyValue.pair(userId, user);
                })
                .to(Topics.USER_OUTPUT);

        KStream<Long, User> stream2 = sourceStreamUsers
                .map((userId, user) -> {
                    LOGGER.debug("Proxying2... " + userId + ": " + user.username + " " + user.queueState);
                    return KeyValue.pair(userId, user);
                })
                .filter((userId, user) -> user.isOnline && User.QUEUE_STATE_QUEUED.equals(user.queueState))
                .map((userId, user) -> {
                    LOGGER.trace("*** filtered " + userId + ": " + user.username + " " + user.level);
                    return KeyValue.pair(userId, user);
                });

        KTable<Integer, UserAggregator> table1 = stream2
                .groupBy((userId, user) -> user.level, Serialized.with(Serdes.Integer(), new UserSerde()))
                .aggregate(
                        () -> new UserAggregator(),
                        (aggKey, newValue, aggValue) -> {
                            LOGGER.trace("*** aggr " + aggKey + " user " + newValue.username);
                            aggValue.add(newValue);
                            return aggValue;
                        },
                        Materialized.<Integer, UserAggregator, KeyValueStore<Bytes, byte[]>>as("store-aggr-level-users")
                            .withKeySerde(Serdes.Integer())
                            .withValueSerde(new UserAggregator.AggregatorSerde())
                ).mapValues(values -> {
                    if (values != null) {
                        values.getList().forEach(value -> {
                            LOGGER.trace("*** after material values: " + value.username);
                        });
                    }
                    return values;
                });

        table1
                .toStream()
                .filter((level, userAggr) -> userAggr != null)
                .map((level, userAggr) -> {
                    LOGGER.trace("*** after aggr " + level + ": " + userAggr.getList().size());
                    return KeyValue.pair(level, userAggr);
                })
                .filter((level, userAggr) -> userAggr.getList().size() >= 4)
                .flatMap((level, userAggr) -> {
                    LOGGER.debug("Found enough users for level " + level);
                    List<User> users = new ArrayList<>(userAggr.getList());
                    users.sort((user1, user2) -> user1.queueStateUpdatedAt.compareTo(user2.queueStateUpdatedAt));
                    List<User> userSublist = users.subList(0, 4);
                    Match match = new Match();
                    match.id = UUID.randomUUID().toString();
                    match.startedAt = new Date();
                    match.endedAt = null;
                    match.userIds = userSublist.stream().map(u -> u.id).collect(Collectors.toList());
                    return Collections.singleton(KeyValue.pair(match.id, match));
                }).to(Topics.MATCH_INPUT, Produced.with(Serdes.String(), new MatchSerde()));
        return builder.build();
    }
}
