package services.serializers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import services.entities.Match;

import java.io.IOException;
import java.util.Map;

public class MatchSerializer implements Serializer<Match> {
    private static final Logger LOGGER = LogManager.getLogger(MatchSerializer.class);

    @Override
    public void configure(Map<String, ?> map, boolean b) {

    }

    @Override
    public byte[] serialize(String s, Match match) {
        ObjectMapper objectMapper = new ObjectMapper();
        byte[] bytes = null;
        try {
            bytes = objectMapper.writeValueAsBytes(match);
        } catch (IOException e) {
            LOGGER.error("Error serializing match", e);
        }
        return bytes;
    }

    @Override
    public void close() {

    }
}
