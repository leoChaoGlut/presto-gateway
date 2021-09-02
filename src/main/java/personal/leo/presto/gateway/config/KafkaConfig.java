package personal.leo.presto.gateway.config;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import personal.leo.presto.gateway.config.props.KafkaProps;

import java.util.Properties;

@Configuration
public class KafkaConfig {

    @ConditionalOnProperty(prefix = "kafka", name = "enabled")
    @Bean
    public KafkaProducer kafkaProducer(KafkaProps kafkaProps) {
        final Properties kafkaProducerProps = new Properties();
        kafkaProducerProps.put("bootstrap.servers", kafkaProps.getBootstrapServers());
        kafkaProducerProps.put("acks", "0");
        kafkaProducerProps.put("key.serializer", StringSerializer.class.getName());
        kafkaProducerProps.put("value.serializer", StringSerializer.class.getName());

        return new KafkaProducer<>(kafkaProducerProps);
    }
}
