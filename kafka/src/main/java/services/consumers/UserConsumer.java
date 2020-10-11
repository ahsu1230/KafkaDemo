package services.consumers;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import services.common.Topics;
import services.entities.User;
import services.serializers.UserDeserializer;
import services.stores.UserStore;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Properties;

import static services.common.KafkaUtils.BOOTSTRAP_ADDRESS_LOCAL;

public class UserConsumer {
    private static final Logger LOGGER = LogManager.getLogger(UserConsumer.class);
    private KafkaConsumer<Long, User> consumer;
    private boolean isClosed;

    public UserConsumer() {
        this.isClosed = false;
        Properties props = configureProperties();
        this.consumer = new KafkaConsumer<>(props);
    }

    public void consume() {
        if (isClosed) {
            LOGGER.warn("Consumer has been closed. No more consumptions allowed");
            return;
        }

        LOGGER.trace("Consuming...");
        try {
            consumer.subscribe(Collections.singleton(Topics.USER_OUTPUT));
            ConsumerRecords<Long, User> records = consumer.poll(Duration.of(1000, ChronoUnit.MILLIS));
            for (ConsumerRecord<Long, User> record : records) {
                User user = record.value();
                UserStore.upsertUser(user);
            }
            LOGGER.trace("Consumed! " + records.count());
        } catch (KafkaException e) {
            LOGGER.error("Kafka error during consumption", e);
        } finally {
            consumer.close();
            LOGGER.trace("Consumer closing.");
        }
    }

    public void close() {
        this.isClosed = true;
    }

    private Properties configureProperties() {
        Properties props = new Properties();
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, "UserConsumer");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "ConsumerGroupUser");
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_ADDRESS_LOCAL);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, UserDeserializer.class.getName());
        return props;
    }
}
