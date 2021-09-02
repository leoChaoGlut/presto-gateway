package personal.leo.presto.gateway.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import personal.leo.presto.gateway.config.props.KafkaProps;
import personal.leo.presto.gateway.config.props.PrestoGatewayProps;
import personal.leo.presto.gateway.constants.CacheNames;
import personal.leo.presto.gateway.constants.CacheResolverNames;
import personal.leo.presto.gateway.constants.Keys;
import personal.leo.presto.gateway.mapper.prestogateway.QueryMapper;
import personal.leo.presto.gateway.mapper.prestogateway.po.QueryPO;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.TimeZone;

@Slf4j
@Service
@CacheConfig(cacheNames = CacheNames.QUERY_ID_CACHE, cacheResolver = CacheResolverNames._10minCacheResolver)
public class QueryService {

    private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    PrestoGatewayProps prestoGatewayProps;
    @Autowired
    KafkaProps kafkaProps;

    @PostConstruct
    private void postConstruct() {
        //TODO 因时区不一致导致kafka里的时间不是期望的时间.这里需要自行修改...
        sdf.setTimeZone(TimeZone.getTimeZone(prestoGatewayProps.getSdfTimeZoneId()));
    }

    @Autowired
    QueryMapper queryMapper;
    @Autowired(required = false)
    KafkaProducer<String, String> kafkaProducer;

    @CachePut(key = "#queryId")
    public String saveQueryId(String queryId, String coordinatorUrl, Map<String, Object> pairs) {
        final QueryPO query = new QueryPO()
                .setQuery_id(queryId)
                .setCoordinator_url(coordinatorUrl)
                .setJson(JSON.toJSONString(pairs));
        queryMapper.insert(query);
        return coordinatorUrl;
    }

    @Cacheable
    public String fetchCoordinatorUrl(String queryId) {
        return queryMapper.selectCoordinatorUrl(queryId);
    }

    @Async
    public void sendMetrics(String queryId, String respBody) {
        final String stats = "stats";
        final String state = "state";
        final String totalSplits = "totalSplits";
        final String cpuTimeMillis = "cpuTimeMillis";
        final String wallTimeMillis = "wallTimeMillis";
        final String elapsedTimeMillis = "elapsedTimeMillis";
        final String processedRows = "processedRows";
        final String processedBytes = "processedBytes";
        final String physicalInputBytes = "physicalInputBytes";
        final String peakMemoryBytes = "peakMemoryBytes";
        final String error = "error";
        final String FAILED = "FAILED";
        final String xPrestoUser = "x-presto-user";
        final String xPrestoSource = "x-presto-source";

        final JSONObject respJsonObject = JSON.parseObject(respBody);
        final JSONObject statsObject = respJsonObject.getJSONObject(stats);
        if (statsObject != null) {
            final String stateValue = statsObject.getString(state);
            if (StringUtils.equalsAny(stateValue, FAILED, "FINISHED")) {
                final JSONObject msgObject = new JSONObject()
                        .fluentPut("queryId", queryId)
                        .fluentPut(state, stateValue)
                        .fluentPut(totalSplits, statsObject.getLongValue(totalSplits))
                        .fluentPut(cpuTimeMillis, statsObject.getLongValue(cpuTimeMillis))
                        .fluentPut(wallTimeMillis, statsObject.getLongValue(wallTimeMillis))
                        .fluentPut(elapsedTimeMillis, statsObject.getLongValue(elapsedTimeMillis))
                        .fluentPut(processedRows, statsObject.getLongValue(processedRows))
                        .fluentPut(processedBytes, statsObject.getLongValue(processedBytes))
                        .fluentPut(physicalInputBytes, statsObject.getLongValue(physicalInputBytes))
                        .fluentPut(peakMemoryBytes, statsObject.getLongValue(peakMemoryBytes));

                if (StringUtils.equalsAny(stateValue, FAILED)) {
                    msgObject.put(error, respJsonObject.getJSONObject(error));
                }

                final QueryPO queryPO = queryMapper.selectById(queryId);
                if (queryPO != null && StringUtils.isNotBlank(queryPO.getJson())) {
                    final Map<String, Object> json = JSON.parseObject(queryPO.getJson(), Map.class);
                    final Map<String, String> headers = (Map<String, String>) json.get(Keys.headers);

                    msgObject
                            .fluentPut(Keys.createTime, sdf.format(queryPO.getCreate_time()))
                            .fluentPut(Keys.coordinator, queryPO.getCoordinator_url())
                            .fluentPut(Keys.sql, json.get(Keys.sql))
                            .fluentPut(xPrestoUser, headers.get(xPrestoUser))
                            .fluentPut(xPrestoSource, headers.get(xPrestoSource))
                    ;

                    headers.remove(xPrestoUser);
                    headers.remove(xPrestoSource);

                    msgObject.put(Keys.headers, headers);

                }
                kafkaProducer.send(new ProducerRecord<>(kafkaProps.getTopic(), JSON.toJSONString(msgObject)));
            }
        }
    }
}
