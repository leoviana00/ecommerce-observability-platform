package io.viana.monitor_health_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

@Configuration
public class HealthHttpConfig {

    @Bean
    public RestTemplate healthRestTemplate(MonitorConfig config) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(config.getDefaultTimeoutMs());
        factory.setReadTimeout(config.getDefaultTimeoutMs());
        return new RestTemplate(factory);
    }

    @Bean(name = "healthCheckExecutor")
    public TaskExecutor healthCheckExecutor(MonitorConfig config) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(config.getHealthCheckThreadPool().getCoreSize());
        executor.setMaxPoolSize(config.getHealthCheckThreadPool().getMaxSize());
        executor.setQueueCapacity(config.getHealthCheckThreadPool().getQueueCapacity());
        executor.setThreadNamePrefix("health-check-");
        executor.initialize();
        return executor;
    }
}
