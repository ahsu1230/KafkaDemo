package projects.ahsu.examples;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Properties;

public class HelloProducer {
    private static final Logger LOGGER = LogManager.getLogger(HelloProducer.class);

    public static void main(String[] args) {
        String topicName;
        int numEvents;

        if (args.length != 2) {
            System.out.println("Please provide command line arguments: topicName numEvents");
            System.exit(-1);
        }

        // Takes in a string topic name & an integer
        topicName = args[0];
        numEvents = Integer.valueOf(args[1]);

        // Start Producer
        LOGGER.info("Starting HelloProducer...");
        LOGGER.debug("topicName=" + topicName + ", numEvents=" + numEvents);
        LOGGER.trace("Creating Kafka Producer...");
        Properties props = new Properties();
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "HelloProducer");
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        KafkaProducer<Integer, String> producer = new KafkaProducer<>(props);
        LOGGER.trace("Start sending messages...");
        try {
            for (int i = 1; i <= numEvents; i++) {
                producer.send(new ProducerRecord<>(topicName, i, "Simple Message-" + i));
            }
        } catch (KafkaException e) {
            LOGGER.error("Exception occurred – Check log for more details.\n" + e.getMessage());
            System.exit(-1);
        } finally {
            LOGGER.info("Finished HelloProducer – Closing Kafka Producer.");
            producer.close();
        }
    }
}
