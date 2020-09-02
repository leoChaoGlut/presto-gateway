package personal.leo.presto.gateway.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.ScheduledThreadPoolExecutor;

@Configuration
@EnableScheduling
public class ScheduleConfig implements SchedulingConfigurer {
    /**
     * 最多支持 schedulePoolSize 个@Schedule并发执行
     *
     * @param taskRegistrar
     */
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        final int schedulePoolSize = Runtime.getRuntime().availableProcessors();
        taskRegistrar.setScheduler(new ScheduledThreadPoolExecutor(schedulePoolSize));
    }
}
