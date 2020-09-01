package personal.leo.presto.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PrestoGatewayLauncher {
    public static void main(String[] args) {
        try {
            SpringApplication.run(PrestoGatewayLauncher.class, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
