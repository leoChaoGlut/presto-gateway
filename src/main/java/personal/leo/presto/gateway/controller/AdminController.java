package personal.leo.presto.gateway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import personal.leo.presto.gateway.mapper.prestogateway.po.CoordinatorPO;
import personal.leo.presto.gateway.service.CoordinatorService;

import java.util.List;

@RestController
@RequestMapping("admin")
public class AdminController {

    @Autowired
    CoordinatorService coordinatorService;

    @GetMapping("addCoordinator")
    public List<CoordinatorPO> addCoordinator(String host, int port) {
        return coordinatorService.addCoordinator(host, port);
    }

    @GetMapping("removeCoordinator")
    public List<CoordinatorPO> removeCoordinator(String host, int port) {
        coordinatorService.removeCoordinator(host, port);
        return coordinatorService.getCoordinators();
    }

    @GetMapping("getCoordinators")
    public List<CoordinatorPO> getCoordinators() {
        return coordinatorService.getCoordinators();
    }


}
