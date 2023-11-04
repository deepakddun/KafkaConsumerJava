


import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import example.avro.Person;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;


/**
 * 
 */
public class KafkaAvroMessageConsumer {

	private static final String TOPIC_NAME = "person";
	private static final String KAFKA_SERVER_ADDRESS = "localhost:9092";
	private static final String SCHEMA_REGISTRY_SERVER_URL = "http://localhost:8081";
	private static final String CONSUMER_GROUP_ID = "customer-message-consumer-group";
	
	private static final Logger log = LoggerFactory.getLogger(KafkaAvroMessageConsumer.class.getSimpleName());
    public static void main(final String[] args) {
    	// Kafka Consumer Configurations
        final Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_SERVER_ADDRESS);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, CONSUMER_GROUP_ID);
        properties.put("schema.registry.url", SCHEMA_REGISTRY_SERVER_URL);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        properties.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true); 
        System.out.println("Starting producer");
        try(final Consumer<String, Person> consumer = new KafkaConsumer<>(properties)){
        	consumer.subscribe(Collections.singleton(TOPIC_NAME));
        	while (true) {
        		final ConsumerRecords<String, Person> records = consumer.poll(Duration.ofMillis(100));
        		
        		for (final ConsumerRecord<String, Person> record : records) {
        			System.out.println(record.value().getFirstName().toString());
        			System.out.println(record.value());
        			System.out.println(record.value().getAddress());
        		}
        		consumer.commitAsync();
        	}
        }
    }

}
