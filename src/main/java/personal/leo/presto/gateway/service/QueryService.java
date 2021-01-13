package personal.leo.presto.gateway.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.ParserConfig;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import personal.leo.presto.gateway.constants.CacheNames;
import personal.leo.presto.gateway.constants.CacheResolverNames;
import personal.leo.presto.gateway.mapper.prestogateway.QueryMapper;
import personal.leo.presto.gateway.mapper.prestogateway.po.QueryPO;
import personal.leo.presto.gateway.utils.Duration;

@Slf4j
@Service
@CacheConfig(cacheNames = CacheNames.QUERY_ID_CACHE, cacheResolver = CacheResolverNames._10minCacheResolver)
public class QueryService {

    @Autowired
    QueryMapper queryMapper;

    static {
        //modify autoType is not support
        ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
    }

    private BlockingQueue<QueryPO> queryQueue = new LinkedBlockingQueue<>(65536);

    @CachePut(key = "#queryId")
    public String saveQueryId(String queryId, String coordinatorUrl) {
//        log.info("saveQueryId: " + queryId + "->" + coordinatorUrl);
        final QueryPO query = QueryPO.builder().query_id(queryId).coordinator_url(coordinatorUrl)
                .build();
        queryMapper.insert(query);
        return coordinatorUrl;
    }

    @Cacheable
    public String fetchCoordinatorUrl(String queryId) {
        final String coordinatorUrl = queryMapper.selectCoordinatorUrl(queryId);
//        log.info("fetchCoordinatorUrl: " + queryId + "->" + coordinatorUrl);
        return coordinatorUrl;
    }

    public void addQuery(QueryPO queryPO) {
        queryQueue.offer(queryPO);
    }

    public void fetchQueryInfo() {
        while (true) {
            final QueryPO po = queryQueue.poll();
            if (po == null) {
                break;
            }
            saveQueryInfo(po);
        }
    }


    private void saveQueryInfo(QueryPO queryPO) {
        final QueryPO queryInfo = getQueryInfo(queryPO);
        queryMapper.saveQueryInfo(queryInfo);
    }

    private QueryPO getQueryInfo(QueryPO queryPO) {
        final String coordinatorUrl = queryPO.getCoordinator_url();
        final String queryId = queryPO.getQuery_id();
        final JSONObject jsonObject = getQueryJson(coordinatorUrl, queryId);
        if (jsonObject != null){
            final String state = jsonObject.getString("state");
            final String elapsedTime = jsonObject.getJSONObject("queryStats")
                    .getString("elapsedTime");
            final Duration duration = Duration.valueOf(elapsedTime);
            final int queryTime = (int) duration.convertTo(TimeUnit.MILLISECONDS).getValue();
            final List<String> groupIds = jsonObject.getJSONArray("resourceGroupId").toJavaList(String.class);
            final String groupId = String.join(".", groupIds);
            final String user = jsonObject.getJSONObject("session").getString("user");
            final String source = jsonObject.getJSONObject("session").getString("source");
            final String sql = jsonObject.getString("query");
            queryPO.setResource_group(groupId);
            queryPO.setUser(user);
            queryPO.setSource(source);
            queryPO.setSql(sql);
            queryPO.setStatus(state);
            queryPO.setElapsed_time(queryTime);

            if (state.equalsIgnoreCase("FINISHED")) {

            }else if (state.equalsIgnoreCase("FAILED")){
                final String errorType = jsonObject.getJSONObject("errorCode").getString("name");
                queryPO.setError_type(errorType);
            }else {
                //查询未结束 重新进入队列
                addQuery(queryPO);
            }
        }
        return queryPO;
    }

    private JSONObject getQueryJson(String coordinatorUrl,String  queryId) {
        try {
            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

                final HttpGet get = new HttpGet(coordinatorUrl + "/v1/query/" + queryId);
                get.addHeader("X-Presto-User","guest");
                get.setConfig(
                        RequestConfig.custom()
                                .setSocketTimeout(3000)
                                .build()
                );
                try (final CloseableHttpResponse resp = httpClient.execute(get)) {
                    if (Objects.equals(resp.getStatusLine().getStatusCode(), HttpStatus.SC_OK)) {
                        final String respBody = IOUtils
                                .toString(resp.getEntity().getContent(), StandardCharsets.UTF_8);
                        return JSON.parseObject(respBody);
                    }
                }
            }
        } catch (Exception e) {
            log.error("fetch query info error:{}", e);
        }
        return null;
    }
}

