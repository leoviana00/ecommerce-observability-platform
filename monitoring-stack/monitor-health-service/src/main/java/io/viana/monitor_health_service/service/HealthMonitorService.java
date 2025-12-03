package io.viana.monitor_health_service.service;

import io.viana.monitor_health_service.dto.HealthTarget;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class HealthMonitorService {

    private final HealthTargetsRegistry registry;

    public HealthMonitorService(HealthTargetsRegistry registry) {
        this.registry = registry;
    }

    public List<HealthTarget> getTargets() {
        return registry.getAll();
    }
}
