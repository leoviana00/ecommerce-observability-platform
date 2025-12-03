package io.viana.monitor_health_service.scheduler;

import io.viana.monitor_health_service.config.MonitorConfig;
import io.viana.monitor_health_service.service.LocalFileTargetsLoader;
import io.viana.monitor_health_service.service.HealthTargetsRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TargetsRefreshScheduler {

    private final MonitorConfig config;
    private final LocalFileTargetsLoader loader;
    private final HealthTargetsRegistry registry;

    public TargetsRefreshScheduler(
            MonitorConfig config,
            LocalFileTargetsLoader loader,
            HealthTargetsRegistry registry) {

        this.config = config;
        this.loader = loader;
        this.registry = registry;

        // initial load
        refresh();
    }

    @Scheduled(fixedDelayString = "#{${monitor.refresh-interval-seconds:300} * 1000}")
    public void refresh() {

        log.info("Refreshing targets from local JSON file...");

        var list = loader.readTargets();

        if (!list.isEmpty()) {
            registry.update(list);
            log.info("Loaded {} targets", list.size());
        } else {
            log.warn("No targets loaded; keeping previous configuration");
        }
    }
}
