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
public class LocalFileTargetsLoader {

    private final MonitorConfig config;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public LocalFileTargetsLoader(MonitorConfig config) {
        this.config = config;
    }

    public List<HealthTarget> readTargets() {

        String path = config.getLocalJsonPath();

        if (path == null || path.isBlank()) {
            log.warn("monitor.local-json-path not configured");
            return Collections.emptyList();
        }

        try {
            log.info("Loading targets from file {}", path);

            File file = new File(path);
            if (!file.exists()) {
                log.error("File not found: {}", path);
                return Collections.emptyList();
            }

            JsonNode json = objectMapper.readTree(file);

            JsonNode targetsNode = json.get("targets");
            if (targetsNode == null || !targetsNode.isArray()) {
                log.warn("Missing or invalid 'targets' array");
                return Collections.emptyList();
            }

            return objectMapper.readerForListOf(HealthTarget.class)
                    .readValue(targetsNode);

        } catch (Exception e) {
            log.error("Failed to load targets from {}", path, e);
            return Collections.emptyList();
        }
    }
}
