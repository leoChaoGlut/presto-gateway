package personal.leo.presto.gateway.mapper.prestogateway.po;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class CoordinatorPO {
    private String host;
    private int port;
    private boolean active;
}
