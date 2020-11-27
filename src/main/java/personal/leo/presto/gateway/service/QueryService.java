package personal.leo.presto.gateway.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import personal.leo.presto.gateway.mapper.prestogateway.QueryMapper;
import personal.leo.presto.gateway.mapper.prestogateway.po.QueryPO;

@Slf4j
@Service
public class QueryService {

    @Autowired
    QueryMapper queryMapper;

    @Autowired
    RedisService redisService;

    public String saveQueryId(String queryId, String coordinatorUrl) {
//        log.info("saveQueryId: " + queryId + "->" + coordinatorUrl);
        final QueryPO query = QueryPO.builder().query_id(queryId).coordinator_url(coordinatorUrl).build();
        queryMapper.insert(query);
        if (redisService.isCacheEnabled()) {
            redisService.put(queryId, coordinatorUrl);
        }
        return coordinatorUrl;
    }

    public String fetchCoordinatorUrl(String queryId) {
        String coordinatorUrl = null;
        if (redisService.isCacheEnabled()) {
            coordinatorUrl = redisService.get(queryId);
        }
        if (StringUtils.isBlank(coordinatorUrl)) {
            coordinatorUrl = queryMapper.selectCoordinatorUrl(queryId);
        }
//            log.info("fetchCoordinatorUrl : " + queryId + "->" + coordinatorUrl);
        return coordinatorUrl;
    }
}
