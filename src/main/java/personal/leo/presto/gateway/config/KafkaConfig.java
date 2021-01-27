package personal.leo.presto.gateway.config;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class KafkaConfig {
    @Bean
    public KafkaProducer kafkaProducer(@Value("${kafka.bootstrap.servers}") String bootstrapServers) {
        final Properties kafkaProducerProps = new Properties();
        kafkaProducerProps.put("bootstrap.servers", bootstrapServers);
        kafkaProducerProps.put("acks", "0");
        kafkaProducerProps.put("key.serializer", StringSerializer.class.getName());
        kafkaProducerProps.put("value.serializer", StringSerializer.class.getName());

        return new KafkaProducer<>(kafkaProducerProps);
    }
}
