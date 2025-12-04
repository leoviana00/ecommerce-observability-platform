package io.viana.monitor_alert_dispatcher.service;

import io.viana.monitor_alert_dispatcher.dto.AlertmanagerAlert;
import io.viana.monitor_alert_dispatcher.dto.AlertmanagerWebhookPayload;
import io.viana.monitor_alert_dispatcher.dto.NormalizedAlertDto;
import io.viana.monitor_alert_dispatcher.telegram.TelegramAlertDispatcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlertDispatchService {

    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_INSTANT;

    private final TelegramAlertDispatcher telegramDispatcher;

    public void processAlertmanagerPayload(AlertmanagerWebhookPayload payload) {
        if (payload.getAlerts() == null || payload.getAlerts().isEmpty()) {
            return;
        }

        List<NormalizedAlertDto> normalized = new ArrayList<>();

        for (AlertmanagerAlert amAlert : payload.getAlerts()) {
            normalized.add(normalize(payload, amAlert));
        }

        normalized.forEach(alert -> {
            logAlert(alert);
            telegramDispatcher.dispatch(alert);
        });
    }

    private NormalizedAlertDto normalize(AlertmanagerWebhookPayload payload, AlertmanagerAlert amAlert) {
        Map<String, String> labels = amAlert.getLabels();
        Map<String, String> annotations = amAlert.getAnnotations();

        String alertName = get(labels, "alertname");
        String service = get(labels, "service");
        String severity = get(labels, "severity");
        String summary = get(annotations, "summary");

        return NormalizedAlertDto.builder()
                .status(payload.getStatus())
                .alertName(alertName)
                .service(service)
                .severity(severity)
                .summary(summary)
                .build();
    }

    private void logAlert(NormalizedAlertDto alert) {
        log.info("ALERT {}: service={} severity={}",
                alert.getAlertName(),
                alert.getService(),
                alert.getSeverity());
    }

    private String get(Map<String, String> map, String key) {
        if (map == null) return null;
        return map.getOrDefault(key, "unknown");
    }
}
