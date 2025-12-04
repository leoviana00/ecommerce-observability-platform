package io.viana.monitor_health_service.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "monitor")
public class MonitorConfig {

    /**
     * Path local para JSON contendo a lista de serviços monitorados.
     */
    private String localJsonPath;

    /**
     * Timeout padrão para requests HTTP (ms).
     */
    private int defaultTimeoutMs = 2000;

    /**
     * Intervalo entre refresh do JSON de targets (s).
     */
    private int refreshIntervalSeconds = 300;

    /**
     * Intervalo entre execuções de health-check (s).
     */
    private int healthCheckIntervalSeconds = 30;

    /**
     * URL base do monitor-state-manager (ex: http://monitor-state-manager:8092).
     */
    private String stateManagerUrl;

    /**
     * Configuração do pool de threads para health checks.
     */
    private ThreadPoolProperties healthCheckThreadPool = new ThreadPoolProperties();

    @Data
    public static class ThreadPoolProperties {
        private int coreSize = 4;
        private int maxSize = 16;
        private int queueCapacity = 100;
    }
}
