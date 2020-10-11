package services.serializers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import services.entities.MatchHistory;

import java.io.IOException;
import java.util.Map;

public class MatchHistorySerializer implements Serializer<MatchHistory> {
    private static final Logger LOGGER = LogManager.getLogger(MatchHistorySerializer.class);

    @Override
    public void configure(Map<String, ?> map, boolean b) {

    }

    @Override
    public byte[] serialize(String s, MatchHistory matchHistory) {
        ObjectMapper objectMapper = new ObjectMapper();
        byte[] bytes = null;
        try {
            bytes = objectMapper.writeValueAsBytes(matchHistory);
        } catch (IOException e) {
            LOGGER.error("Error serializing match-history", e);
        }
        return bytes;
    }

    @Override
    public void close() {

    }
}
