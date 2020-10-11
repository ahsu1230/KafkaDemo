package services.entities;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class UserAggregator {
    private static final Logger LOGGER = LogManager.getLogger(UserAggregator.class);
    private List<User> list = new ArrayList<>();

    public UserAggregator() {}

    public void add(User user) {
        Set<Long> userIds = list.stream().map(u -> u.id).collect(Collectors.toSet());
        if (!userIds.contains(user.id)) {
            list.add(user);
        }
    }

    public List<User> getList() {
        return list;
    }

    public static class AggregatorSerializer implements Serializer<UserAggregator> {
        @Override
        public void configure(Map<String, ?> map, boolean b) {

        }

        @Override
        public byte[] serialize(String s, UserAggregator userAggregator) {
            ObjectMapper objectMapper = new ObjectMapper();
            byte[] bytes = null;
            try {
                bytes = objectMapper.writeValueAsBytes(userAggregator);
            } catch (IOException e) {
                LOGGER.error("Error serializing user", e);
            }
            return bytes;
        }

        @Override
        public void close() {

        }
    }

    public static class AggregatorDeserializer implements Deserializer<UserAggregator> {

        @Override
        public void configure(Map<String, ?> map, boolean b) {

        }

        @Override
        public UserAggregator deserialize(String s, byte[] bytes) {
            ObjectMapper mapper = new ObjectMapper();
            UserAggregator aggr = null;
            try {
                aggr = mapper.readValue(bytes, UserAggregator.class);
            } catch (IOException e) {
                LOGGER.error("Error deserializing", e);
            }
            return aggr;
        }

        @Override
        public void close() {

        }
    }

    public static class AggregatorSerde implements Serde<UserAggregator> {
        UserAggregator.AggregatorSerializer serializer = new UserAggregator.AggregatorSerializer();
        UserAggregator.AggregatorDeserializer deserializer = new UserAggregator.AggregatorDeserializer();

        @Override
        public void configure(Map<String, ?> configs, boolean isKey) {
            serializer.configure(configs, isKey);
            deserializer.configure(configs, isKey);
        }

        @Override
        public void close() {
            serializer.close();
            deserializer.close();
        }

        @Override
        public Serializer<UserAggregator> serializer() {
            return serializer;
        }

        @Override
        public Deserializer<UserAggregator> deserializer() {
            return deserializer;
        }
    }
}
