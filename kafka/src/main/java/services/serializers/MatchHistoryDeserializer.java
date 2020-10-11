package services.serializers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import services.entities.Match;
import services.entities.MatchHistory;

import java.io.IOException;
import java.util.Map;

public class MatchHistoryDeserializer implements Deserializer<MatchHistory> {
    private static final Logger LOGGER = LogManager.getLogger(MatchHistoryDeserializer.class);

    @Override
    public void configure(Map<String, ?> map, boolean b) {

    }

    @Override
    public MatchHistory deserialize(String s, byte[] bytes) {
        ObjectMapper mapper = new ObjectMapper();
        MatchHistory matchHistory = null;
        try {
            matchHistory = mapper.readValue(bytes, MatchHistory.class);
        } catch (IOException e) {
            LOGGER.error("Error deserializer match-history", e);
        }
        return matchHistory;
    }

    @Override
    public void close() {

    }
}
