package services.serializers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import services.entities.User;

import java.io.IOException;
import java.util.Map;

public class UserDeserializer implements Deserializer<User> {
    private static final Logger LOGGER = LogManager.getLogger(UserDeserializer.class);

    @Override
    public void configure(Map map, boolean b) {

    }

    @Override
    public User deserialize(String s, byte[] bytes) {
        ObjectMapper mapper = new ObjectMapper();
        User user = null;
        try {
            user = mapper.readValue(bytes, User.class);
        } catch (IOException e) {
            LOGGER.error("Error deserializer user", e);
        }
        return user;
    }

    @Override
    public void close() {

    }
}
