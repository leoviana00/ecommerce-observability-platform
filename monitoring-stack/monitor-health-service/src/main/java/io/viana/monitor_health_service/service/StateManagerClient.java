package io.viana.monitor_health_service.service;

import io.viana.monitor_health_service.config.MonitorConfig;
import io.viana.monitor_health_service.dto.HealthStateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class StateManagerClient {

    private final RestTemplate healthRestTemplate;
    private final MonitorConfig monitorConfig;

    public void sendHealthState(HealthStateDto dto) {
        String baseUrl = monitorConfig.getStateManagerUrl();

        if (baseUrl == null || baseUrl.isBlank()) {
            log.warn("monitor.state-manager-url not configured; skipping sendHealthState");
            return;
        }

        String url = baseUrl + "/state/health";

        try {
            ResponseEntity<Void> response = healthRestTemplate.postForEntity(url, dto, Void.class);
            log.debug("Sent health state for [{}] to state-manager. Status={}",
                    dto.getService(), response.getStatusCode());
        } catch (Exception e) {
            log.error("Failed to send health state for [{}] to state-manager at {}",
                    dto.getService(), url, e);
        }
    }
}
