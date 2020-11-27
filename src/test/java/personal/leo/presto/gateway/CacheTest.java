package personal.leo.presto.gateway;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import personal.leo.presto.gateway.service.QueryService;

/**
 * @Auhtor chenliang
 * @Date 2020/11/27
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class CacheTest {

    @Autowired
    QueryService queryService;

    private final String queryId = "#20201127_071053_28476_nivp4";
    private final String coordinatorUrl = "etl01:10100";

    @Test
    public void setCache() {
        System.out.println(queryService.saveQueryId(queryId, coordinatorUrl));
    }

    @Test
    public void getByCache() {
        System.out.println(queryService.fetchCoordinatorUrl(queryId));
    }

}
