package personal.leo.presto.gateway.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import personal.leo.presto.gateway.service.QueryService;

/**
 * @Auhtor chenliang
 * @Date 2021/1/12
 **/
@Slf4j
@Component
public class QueryInfoManager {

    @Autowired
    private QueryService queryService;

    @Scheduled(fixedDelay = 5_000L)
    private void fetchQueryInfo() {
        queryService.fetchQueryInfo();
        log.info("fetch query info");
    }

}
