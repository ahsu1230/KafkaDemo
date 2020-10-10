package services.serializers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import services.entities.Match;

import java.io.IOException;
import java.util.Map;

public class MatchDeserializer implements Deserializer<Match> {
    private static final Logger LOGGER = LogManager.getLogger(MatchDeserializer.class);

    @Override
    public void configure(Map<String, ?> map, boolean b) {

    }

    @Override
    public Match deserialize(String s, byte[] bytes) {
        ObjectMapper mapper = new ObjectMapper();
        Match match = null;
        try {
            match = mapper.readValue(bytes, Match.class);
        } catch (IOException e) {
            LOGGER.error("Error deserializer match", e);
        }
        return match;
    }

    @Override
    public void close() {

    }
}
