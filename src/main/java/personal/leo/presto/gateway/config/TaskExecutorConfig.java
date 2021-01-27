package personal.leo.presto.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.task.TaskExecutorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
public class TaskExecutorConfig {

    @Value("${maxRunningTask:300}")
    int maxRunningTask;

    @Bean
    @Primary
    public TaskExecutor taskExecutor() {
        final int poolSize = Runtime.getRuntime().availableProcessors() * maxRunningTask;
        return new TaskExecutorBuilder()
                .corePoolSize(poolSize)
                .maxPoolSize(poolSize)
                .queueCapacity(0)
                .build()
                ;
    }

}
