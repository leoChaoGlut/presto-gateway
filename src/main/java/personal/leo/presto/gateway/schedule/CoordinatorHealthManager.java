package personal.leo.presto.gateway.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import personal.leo.presto.gateway.mapper.prestogateway.CoordinatorMapper;
import personal.leo.presto.gateway.mapper.prestogateway.po.CoordinatorPO;
import personal.leo.presto.gateway.service.CoordinatorService;

import java.util.List;

@Slf4j
@Component
public class CoordinatorHealthManager {

    @Autowired
    CoordinatorService coordinatorService;
    @Autowired
    CoordinatorMapper coordinatorMapper;

    @Scheduled(fixedDelay = 1_000L)
    public void healthCheck() {
        final List<CoordinatorPO> persistencedCoordinators = coordinatorMapper.selectAll();
        final List<CoordinatorPO> cachedCoordinators = coordinatorService.getCoordinators();
        for (CoordinatorPO persistencedCoordinator : persistencedCoordinators) {
            final boolean active = coordinatorService.isActive(persistencedCoordinator);
            if (active) {
                if (!persistencedCoordinator.isActive()) {
                    coordinatorMapper.active(persistencedCoordinator);
                }
                if (!cachedCoordinators.contains(persistencedCoordinator)) {
                    cachedCoordinators.add(persistencedCoordinator);
                }
            } else {
                if (persistencedCoordinator.isActive()) {
                    coordinatorMapper.deactive(persistencedCoordinator);
                }
                cachedCoordinators.remove(persistencedCoordinator);
            }
        }
    }

}
