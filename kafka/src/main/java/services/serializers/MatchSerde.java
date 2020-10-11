package services.serializers;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;
import services.entities.Match;

import java.util.Map;

public class MatchSerde implements Serde<Match> {
    private MatchSerializer serializer = new MatchSerializer();
    private MatchDeserializer deserializer = new MatchDeserializer();

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
    public Serializer<Match> serializer() {
        return serializer;
    }

    @Override
    public Deserializer<Match> deserializer() {
        return deserializer;
    }
}
