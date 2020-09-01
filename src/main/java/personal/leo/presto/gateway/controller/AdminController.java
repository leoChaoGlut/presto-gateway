package personal.leo.presto.gateway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
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

    @GetMapping("reloadCoordinators")
    public List<CoordinatorPO> reloadCoordinators() {
        coordinatorService.reloadCoordinators();
        return coordinatorService.getCoordinators();
    }

    @Retryable(recover = "recover")
    @GetMapping("retryTest")
    public void retryTest() {
        System.out.println("==========");
        throw new RuntimeException("11");
    }

    @Recover
    public void recover(Exception e) {
        System.out.println("2222222222222");
        System.out.println(e.getMessage());
    }

}
