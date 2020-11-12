package personal.leo.presto.gateway.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import personal.leo.presto.gateway.constants.CacheNames;
import personal.leo.presto.gateway.constants.CacheResolverNames;
import personal.leo.presto.gateway.mapper.prestogateway.QueryMapper;
import personal.leo.presto.gateway.mapper.prestogateway.po.QueryPO;

@Slf4j
@Service
@CacheConfig(cacheNames = CacheNames.QUERY_ID_CACHE, cacheResolver = CacheResolverNames._10minCacheResolver)
public class QueryService {

    @Autowired
    QueryMapper queryMapper;

    @CachePut(key = "#queryId")
    public String saveQueryId(String queryId, String coordinatorUrl) {
//        log.info("saveQueryId: " + queryId + "->" + coordinatorUrl);
        final QueryPO query = QueryPO.builder().query_id(queryId).coordinator_url(coordinatorUrl).build();
        queryMapper.insert(query);
        return coordinatorUrl;
    }

    @Cacheable
    public String fetchCoordinatorUrl(String queryId) {
        final String coordinatorUrl = queryMapper.selectCoordinatorUrl(queryId);
//        log.info("fetchCoordinatorUrl: " + queryId + "->" + coordinatorUrl);
        return coordinatorUrl;
    }
}
