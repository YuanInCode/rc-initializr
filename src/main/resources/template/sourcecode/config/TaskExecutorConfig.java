package ${package}.config;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 配置Spring's {@link TaskExecutor}
 */
@Configuration
public class TaskExecutorConfig {

    @Bean("commonTaskExecutor")
    public TaskExecutor taskExecutor() {
        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("CommonTaskExecutor-%d").build();
        int processors = Runtime.getRuntime().availableProcessors();

        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(processors);
        taskExecutor.setMaxPoolSize(processors * 4);
        taskExecutor.setKeepAliveSeconds(60);
        taskExecutor.setQueueCapacity(1000);
        taskExecutor.setThreadFactory(threadFactory);
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return taskExecutor;
    }
}
