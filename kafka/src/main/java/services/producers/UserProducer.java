package services.producers;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import services.common.Topics;
import services.entities.User;
import services.serializers.UserSerializer;

import java.util.Properties;
import static services.common.KafkaUtils.BOOTSTRAP_ADDRESS_LOCAL;

public class UserProducer {
    private static final Logger LOGGER = LogManager.getLogger(UserProducer.class);

    private String topic;
    private boolean isClosed;
    private KafkaProducer<Long, User> producer;

    public UserProducer() {
        this.topic = Topics.USER;
        this.isClosed = false;

        LOGGER.trace("Initializing Kafka Producer");
        Properties props = configureProperties();
        this.producer = new KafkaProducer<>(props);
    }

    public void produce(User user) {
        if (isClosed) {
            LOGGER.warn("Producer has been closed. No more productions allowed");
            return;
        }
        producer.send(new ProducerRecord<>(topic, user.id, user));
    }

    public void close() {
        this.isClosed = true;
    }

    private Properties configureProperties() {
        Properties props = new Properties();
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "UserProducer");
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_ADDRESS_LOCAL);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, UserSerializer.class.getName());
        return props;
    }
}
