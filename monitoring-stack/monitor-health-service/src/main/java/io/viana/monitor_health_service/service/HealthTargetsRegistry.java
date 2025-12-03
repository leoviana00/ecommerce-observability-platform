package io.viana.monitor_health_service.service;

import io.viana.monitor_health_service.dto.HealthTarget;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Component
public class HealthTargetsRegistry {

    private final List<HealthTarget> current = new CopyOnWriteArrayList<>();

    public List<HealthTarget> getAll() {
        return Collections.unmodifiableList(current);
    }

    public void update(List<HealthTarget> targets) {
        current.clear();
        current.addAll(targets);

        log.info("Targets updated: {} active services", targets.size());
    }
}
