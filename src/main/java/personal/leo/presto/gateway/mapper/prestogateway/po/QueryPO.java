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
    private int elapsed_time;
    private String user;
    private String status;
    private String error_type;
    private String sql;
    private String resource_group;
    private String source;
}
