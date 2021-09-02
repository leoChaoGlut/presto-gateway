package personal.leo.presto.gateway.config.props;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KafkaProps {
    private boolean enabled;
    private String topic;
    private String bootstrapServers;
}
