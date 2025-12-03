package io.viana.monitor_health_service.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.viana.monitor_health_service.config.MonitorConfig;
import io.viana.monitor_health_service.dto.HealthTarget;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class TargetLoaderService {

    private final MonitorConfig config;
    private final ObjectMapper mapper = new ObjectMapper();

    public TargetLoaderService(MonitorConfig config) {
        this.config = config;
    }

    public List<HealthTarget> loadTargets() {

        String path = config.getLocalJsonPath();

        if (path == null || path.isBlank()) {
            log.warn("monitor.local-json-path not configured");
            return Collections.emptyList();
        }

        try {
            log.info("Loading targets from file {}", path);

            File file = new File(path);
            JsonNode json = mapper.readTree(file);

            return mapper.readerForListOf(HealthTarget.class)
                    .readValue(json.get("targets"));

        } catch (Exception e) {
            log.error("Failed to load targets from {}", path, e);
            return Collections.emptyList();
        }
    }
}
