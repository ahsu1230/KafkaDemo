package services.serializers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import services.entities.User;

import java.io.IOException;
import java.util.Map;

public class UserSerializer implements Serializer<User> {
    private static final Logger LOGGER = LogManager.getLogger(UserSerializer.class);

    @Override
    public void configure(Map map, boolean b) {

    }

    @Override
    public byte[] serialize(String s, User user) {
        ObjectMapper objectMapper = new ObjectMapper();
        byte[] bytes = null;
        try {
            bytes = objectMapper.writeValueAsBytes(user);
        } catch (IOException e) {
            LOGGER.error("Error serializing user", e);
        }
        return bytes;
    }

    @Override
    public void close() {

    }
}
