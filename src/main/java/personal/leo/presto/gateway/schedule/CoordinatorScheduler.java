package personal.leo.presto.gateway.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import personal.leo.presto.gateway.mapper.prestogateway.po.CoordinatorPO;
import personal.leo.presto.gateway.service.CoordinatorService;

@Slf4j
@Component
public class CoordinatorScheduler {


    @Autowired
    CoordinatorService coordinatorService;

    @Scheduled(fixedDelay = 2_000L)
    public void removeInactiveCoordinator() {
        for (CoordinatorPO coordinator : coordinatorService.getCoordinators()) {
            final boolean isInactive = !coordinatorService.isActive(coordinator);
            if (isInactive) {
                coordinatorService.removeCoordinator(coordinator);
            }
        }
    }

}
