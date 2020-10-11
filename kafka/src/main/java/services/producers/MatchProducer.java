package services.producers;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import services.common.Topics;
import services.entities.Match;
import services.serializers.MatchSerializer;

import java.util.Properties;

import static services.common.KafkaUtils.BOOTSTRAP_ADDRESS_LOCAL;

public class MatchProducer {
    private static final Logger LOGGER = LogManager.getLogger(MatchProducer.class);

    private static final String TOPIC = Topics.MATCH_INPUT;
    private KafkaProducer<String, Match> producer;
    private boolean isClosed;

    public MatchProducer() {
        Properties props = configureProperties();
        this.producer = new KafkaProducer<>(props);
    }

    public void produce(Match match) {
        if (this.isClosed) {
            LOGGER.warn("Producer has been closed. No more productions allowed");
            return;
        }
        producer.send(new ProducerRecord<>(TOPIC, match.id, match));
        LOGGER.trace("Produce!");
    }

    public void close() {
        this.isClosed = true;
    }

    private Properties configureProperties() {
        Properties props = new Properties();
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "MatchProducer");
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_ADDRESS_LOCAL);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, MatchSerializer.class.getName());
        return props;
    }
}
