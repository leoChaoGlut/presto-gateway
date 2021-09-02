package personal.leo.presto.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import personal.leo.presto.gateway.config.props.KafkaProps;
import personal.leo.presto.gateway.config.props.PrestoGatewayProps;

@Configuration
public class PrestoGatewayConfig {

    @Bean
    @ConfigurationProperties("presto-gateway")
    public PrestoGatewayProps prestoGatewayProps() {
        return new PrestoGatewayProps();
    }

    @Bean
    @ConfigurationProperties("kafka")
    public KafkaProps kafkaProps() {
        return new KafkaProps();
    }

}
