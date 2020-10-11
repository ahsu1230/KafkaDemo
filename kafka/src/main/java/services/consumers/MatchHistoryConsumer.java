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
import services.entities.MatchHistory;
import services.entities.MatchStat;
import services.serializers.MatchHistoryDeserializer;
import services.stores.MatchStatsStore;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Properties;

import static services.common.KafkaUtils.BOOTSTRAP_ADDRESS_LOCAL;

public class MatchHistoryConsumer {
    private static final Logger LOGGER = LogManager.getLogger(MatchHistoryConsumer.class);
    private KafkaConsumer<Long, MatchHistory> consumer;
    private boolean isClosed;

    public MatchHistoryConsumer() {
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
            consumer.subscribe(Collections.singleton(Topics.MATCH_INPUT));
            ConsumerRecords<Long, MatchHistory> records = consumer.poll(Duration.of(1000, ChronoUnit.MILLIS));
            LOGGER.trace("Consumed! " + records.count());
            for (ConsumerRecord<Long, MatchHistory> record : records) {
                upsertMatchStat(record.key(), record.value());
            }
        } catch (KafkaException e) {
            LOGGER.error("Kafka error during consumption", e);
        } finally {
            consumer.close();
            LOGGER.trace("Consumer closing.");
        }
    }

    private void upsertMatchStat(long userId, MatchHistory matchHistory) {
        MatchStat stat = MatchStatsStore.getMatchStat(userId);
        if (stat == null) {
            stat = new MatchStat(
                    1,
                    matchHistory.isWinner ? 1 : 0,
                    matchHistory.abandoned ? 1 : 0);
        } else {
            stat.numMatches += 1;
            stat.numWins += (matchHistory.isWinner ? 1 : 0);
            stat.numAbandons += (matchHistory.abandoned ? 1 : 0);
        }
        MatchStatsStore.upsertStat(userId, stat);
    }

    public void close() {
        this.isClosed = true;
    }

    private Properties configureProperties() {
        Properties props = new Properties();
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, "MatchHistoryConsumer");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "ConsumerGroupMatchHistory");
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_ADDRESS_LOCAL);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, MatchHistoryDeserializer.class.getName());
        return props;
    }
}
