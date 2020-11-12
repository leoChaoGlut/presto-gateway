package personal.leo.presto.gateway.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import personal.leo.presto.gateway.mapper.prestogateway.QueryMapper;

@Slf4j
@Component
public class QueryCleaner {

    @Autowired
    QueryMapper queryMapper;

    @Scheduled(fixedDelay = 6 * 60 * 60 * 1000)
    public void cleanQuery() {
        final int count = queryMapper.cleanQuery();
        log.info("cleanQuery: " + count);
    }

}
