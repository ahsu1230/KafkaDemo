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
import services.entities.Match;
import services.serializers.MatchDeserializer;
import services.stores.MatchStore;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Properties;

import static services.common.KafkaUtils.BOOTSTRAP_ADDRESS_LOCAL;

public class MatchConsumer {
    private static final Logger LOGGER = LogManager.getLogger(MatchConsumer.class);
    private String topic;
    private KafkaConsumer<Long, Match> consumer;
    private boolean isClosed;

    public MatchConsumer() {
        this.topic = Topics.MATCH;
        this.isClosed = false;
        Properties props = configureProperties();
        this.consumer = new KafkaConsumer<>(props);
    }

    public void consume() {
        if (isClosed) {
            LOGGER.warn("Consumer has been closed. No more consumptions allowed");
            return;
        }

        LOGGER.trace("Attempting to consume...");
        try {
            consumer.subscribe(Collections.singleton(topic));
            ConsumerRecords<Long, Match> records = consumer.poll(Duration.of(1000, ChronoUnit.MILLIS));
            LOGGER.trace("Consumed! " + records.count());
            for (ConsumerRecord<Long, Match> record : records) {
                MatchStore.upsertMatch(record.value());
            }
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
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, "MatchConsumer");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "ConsumerGroup2");
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_ADDRESS_LOCAL);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, MatchDeserializer.class.getName());
        return props;
    }
}
