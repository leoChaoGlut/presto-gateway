package personal.leo.presto.gateway.mapper.prestogateway.po;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Date;

@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode(of = "query_id")
public class QueryPO {
    private String query_id;
    private String coordinator_url;
    private String json;
    private Date create_time;
}
