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
     * Timeout padrão para requests HTTP.
     */
    private int defaultTimeoutMs = 2000;

    /**
     * Intervalo entre refresh do JSON.
     */
    private int refreshIntervalSeconds = 300;
}
