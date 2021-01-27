package personal.leo.presto.gateway.service;

import com.alibaba.fastjson.JSON;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import personal.leo.presto.gateway.mapper.prestogateway.CoordinatorMapper;
import personal.leo.presto.gateway.mapper.prestogateway.po.CoordinatorPO;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TODO coordinator列表变更时,需要通知其它gateway实例
 * 本类不做coordinator状态管理,由{@link personal.leo.presto.gateway.schedule.CoordinatorHealthManager} 管理
 */
@Slf4j
@Service
public class CoordinatorService {
    @Getter
    final List<CoordinatorPO> coordinators = new CopyOnWriteArrayList<>();

    final AtomicInteger index = new AtomicInteger(0);

    @Autowired
    QueryService queryService;
    @Autowired
    CoordinatorMapper coordinatorMapper;

    @PostConstruct
    public void postConstruct() {
        reloadCoordinators();
    }


    public void reloadCoordinators() {
        final List<CoordinatorPO> activeCoordinators = coordinatorMapper.selectActiveCoordinators();
        this.coordinators.clear();
        if (CollectionUtils.isNotEmpty(activeCoordinators)) {
            this.coordinators.addAll(activeCoordinators);
        }
    }

    public List<CoordinatorPO> addCoordinator(String host, int port) {
        final boolean isActive = checkIsActive(host, port);
        if (isActive) {
            final CoordinatorPO coordinator = CoordinatorPO.builder().host(host).port(port).active(isActive).build();
            final int count = coordinatorMapper.insert(coordinator);
            if (count > 0) {
                coordinators.add(coordinator);
            }
            return coordinators;
        } else {
            throw new RuntimeException("coordinator is inactived -> " + host + ":" + port);
        }
    }

    public boolean checkIsActive(CoordinatorPO coordinator) {
        return checkIsActive(coordinator.getHost(), coordinator.getPort());
    }

    public boolean checkIsActive(String host, int port) {
        try {
            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

                loginPrestoUi(httpClient, host, port);

                final HttpGet get = new HttpGet("http://" + host + ":" + port + "/ui/api/stats");
                get.setConfig(
                        RequestConfig.custom()
                                .setSocketTimeout(3000)
                                .build()
                );

                try (final CloseableHttpResponse resp = httpClient.execute(get)) {
                    if (Objects.equals(resp.getStatusLine().getStatusCode(), HttpStatus.SC_OK)) {
                        final String respBody = IOUtils.toString(resp.getEntity().getContent(), StandardCharsets.UTF_8);

                        final Integer activeWorkers = JSON.parseObject(respBody).getInteger("activeWorkers");
                        if (activeWorkers > 0) {
//                            log.info(host + ":" + port + " isActive");
                            return true;
                        }
                    }

                }
            }
        } catch (Exception e) {
            log.error("isActive error: " + host + ":" + port, e);
        }

        return false;
    }

    private void loginPrestoUi(CloseableHttpClient httpClient, String host, int port) throws IOException {
        final HttpPost post = new HttpPost("http://" + host + ":" + port + "/ui/login");
        post.setEntity(new UrlEncodedFormEntity(Collections.singletonList(
                //TODO 默认未开启ui权限,用guest登陆
                new BasicNameValuePair("username", "guest")
        )));
        try (final CloseableHttpResponse resp = httpClient.execute(post);) {

        }
    }


    public String fetchCoordinatorUrl() {
        if (coordinators.isEmpty()) {
            throw new RuntimeException("No active coordinator");
        }

        synchronized (index) {
            if (index.get() >= coordinators.size()) {
                index.set(0);
            }

            final CoordinatorPO coordinator = coordinators.get(index.get());
            index.incrementAndGet();

            return "http://" + coordinator.getHost() + ":" + coordinator.getPort();
        }
    }

    public void removeCoordinator(String host, int port) {
        final CoordinatorPO coordinator = CoordinatorPO.builder().host(host).port(port).build();
        removeCoordinator(coordinator);
    }

    /**
     * 这里会把数据库里的coordinator信息也给删掉,谨慎使用
     *
     * @param coordinator
     */
    public void removeCoordinator(CoordinatorPO coordinator) {
        int count = coordinatorMapper.remove(coordinator);
        if (count > 0) {
            coordinators.remove(coordinator);
        } else {
            throw new RuntimeException("removeCoordinator failed: " + coordinator);
        }
    }


}
