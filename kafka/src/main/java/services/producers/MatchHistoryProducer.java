package services.producers;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import services.common.Topics;
import services.entities.MatchHistory;
import services.entities.User;
import services.serializers.MatchHistorySerializer;

import java.util.Properties;

import static services.common.KafkaUtils.BOOTSTRAP_ADDRESS_LOCAL;

public class MatchHistoryProducer {
    private static final Logger LOGGER = LogManager.getLogger(MatchHistoryProducer.class);

    private static final String TOPIC = Topics.MATCH_HISTORY;
    private boolean isClosed = false;
    private KafkaProducer<Long, MatchHistory> producer;

    public MatchHistoryProducer() {
        LOGGER.trace("Initializing Kafka Producer");
        Properties props = configureProperties();
        this.producer = new KafkaProducer<>(props);
    }

    public void produce(User user, MatchHistory matchHistory) {
        if (this.isClosed) {
            LOGGER.warn("Producer has been closed. No more productions allowed");
            return;
        }
        producer.send(new ProducerRecord<>(TOPIC, user.id, matchHistory));
    }

    public void close() {
        this.isClosed = true;
    }

    private Properties configureProperties() {
        Properties props = new Properties();
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "MatchHistoryProducer");
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_ADDRESS_LOCAL);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, MatchHistorySerializer.class.getName());
        return props;
    }
}
