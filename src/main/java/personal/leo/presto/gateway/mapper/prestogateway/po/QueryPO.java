package personal.leo.presto.gateway.mapper.prestogateway.po;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "query_id")
public class QueryPO {
    private String query_id;
    private String coordinator_url;
}
