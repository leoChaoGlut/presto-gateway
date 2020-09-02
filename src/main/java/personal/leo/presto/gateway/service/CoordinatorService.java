package personal.leo.presto.gateway.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import personal.leo.presto.gateway.mapper.prestogateway.CoordinatorMapper;
import personal.leo.presto.gateway.mapper.prestogateway.po.CoordinatorPO;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * TODO coordinator列表变更时,需要通知其它gateway实例
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
        synchronized (coordinators) {
            final List<CoordinatorPO> coordinators = coordinatorMapper.selectAll();
            this.coordinators.clear();
            if (CollectionUtils.isNotEmpty(coordinators)) {
                final List<CoordinatorPO> activeCoordinators = coordinators.stream()
                        .filter(this::isActive)
                        .collect(Collectors.toList());
                this.coordinators.addAll(activeCoordinators);
            }
        }
    }

    public List<CoordinatorPO> addCoordinator(String host, int port) {
        final boolean isActive = isActive(host, port);
        if (isActive) {
            final CoordinatorPO coordinator = CoordinatorPO.builder().host(host).port(port).build();
            final int count = coordinatorMapper.insert(coordinator);
            if (count > 0) {
                coordinators.add(coordinator);
            }
            return coordinators;
        } else {
            throw new RuntimeException("coordinator is inactived -> " + host + ":" + port);
        }
    }

    public boolean isActive(CoordinatorPO coordinator) {
        return isActive(coordinator.getHost(), coordinator.getPort());
    }

    public boolean isActive(String host, int port) {
        try {
            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                HttpGet get = new HttpGet("http://" + host + ":" + port + "/v1/info/state");

                try (CloseableHttpResponse resp = httpClient.execute(get)) {
                    if (Objects.equals(resp.getStatusLine().getStatusCode(), HttpStatus.SC_OK)) {
                        final String respBody = IOUtils.toString(resp.getEntity().getContent(), StandardCharsets.UTF_8);
                        if (StringUtils.containsIgnoreCase(respBody, "ACTIVE")) {
                            return true;
                        }
                    }

                }
            }
        } catch (IOException e) {
            log.error("isActive error", e);
        }

        return false;
    }

    public String fetchCoordinatorUrl(String requestUri) {
        final String[] split = StringUtils.splitByWholeSeparator(requestUri, "/");
        final String queryId = split[3];
        return queryService.fetchCoordinatorUrl(queryId);
    }

    public String fetchCoordinatorUrl() {
        if (coordinators.isEmpty()) {
            reloadCoordinators();
            if (coordinators.isEmpty()) {
                throw new RuntimeException("No active coordinator");
            }
        }

        final int index = this.index.get();
        final CoordinatorPO coordinator;
        if (index >= coordinators.size()) {
            coordinator = coordinators.get(0);
            this.index.compareAndSet(index, 0);
        } else {
            coordinator = coordinators.get(index);
            this.index.incrementAndGet();
        }

        return "http://" + coordinator.getHost() + ":" + coordinator.getPort();
    }

    public void removeCoordinator(String host, int port) {
        final CoordinatorPO coordinator = CoordinatorPO.builder().host(host).port(port).build();
        removeCoordinator(coordinator);
    }

    public void removeCoordinator(CoordinatorPO coordinator) {
        int count = coordinatorMapper.remove(coordinator);
        if (count > 0) {
            coordinators.remove(coordinator);
        } else {
            throw new RuntimeException("removeCoordinator failed: " + coordinator);
        }
    }


}
