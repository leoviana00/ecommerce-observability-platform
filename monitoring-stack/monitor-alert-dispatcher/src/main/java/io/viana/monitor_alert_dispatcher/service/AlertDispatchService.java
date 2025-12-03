package io.viana.monitor_alert_dispatcher.service;

import io.viana.monitor_alert_dispatcher.dto.AlertmanagerAlert;
import io.viana.monitor_alert_dispatcher.dto.AlertmanagerWebhookPayload;
import io.viana.monitor_alert_dispatcher.dto.NormalizedAlertDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlertDispatchService {

    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_INSTANT;

    public void processAlertmanagerPayload(AlertmanagerWebhookPayload payload) {
        if (payload.getAlerts() == null || payload.getAlerts().isEmpty()) {
            log.warn("Received Alertmanager payload with no alerts");
            return;
        }

        List<NormalizedAlertDto> normalizedAlerts = new ArrayList<>();

        for (AlertmanagerAlert amAlert : payload.getAlerts()) {
            normalizedAlerts.add(normalize(payload, amAlert));
        }

        // MVP: apenas logar de forma estruturada
        for (NormalizedAlertDto alert : normalizedAlerts) {
            logAlert(alert);
        }

        // FUTURO: aqui você pluga canais
        // sendToTelegram(alert)
        // sendToEmail(alert)
        // sendToWebhook(alert)
    }

    private NormalizedAlertDto normalize(AlertmanagerWebhookPayload payload, AlertmanagerAlert amAlert) {
        Map<String, String> labels = amAlert.getLabels();
        Map<String, String> annotations = amAlert.getAnnotations();

        String alertName = firstNonNull(
                get(labels, "alertname"),
                get(payload.getCommonLabels(), "alertname"),
                "unknown-alert"
        );

        String service = firstNonNull(
                get(labels, "service"),
                get(labels, "job"),
                "unknown-service"
        );

        String severity = firstNonNull(
                get(labels, "severity"),
                get(payload.getCommonLabels(), "severity"),
                "unknown"
        );

        String summary = firstNonNull(
                get(annotations, "summary"),
                get(payload.getCommonAnnotations(), "summary"),
                alertName
        );

        String description = firstNonNull(
                get(annotations, "description"),
                get(payload.getCommonAnnotations(), "description"),
                ""
        );

        String startsAt = amAlert.getStartsAt() != null ? ISO.format(amAlert.getStartsAt()) : null;
        String endsAt = amAlert.getEndsAt() != null ? ISO.format(amAlert.getEndsAt()) : null;

        return NormalizedAlertDto.builder()
                .status(payload.getStatus())
                .alertName(alertName)
                .service(service)
                .severity(severity)
                .summary(summary)
                .description(description)
                .startsAt(startsAt)
                .endsAt(endsAt)
                .build();
    }

    private void logAlert(NormalizedAlertDto alert) {
        log.info(
                "ALERT [{}] status={} service={} severity={} summary=\"{}\"",
                alert.getAlertName(),
                alert.getStatus(),
                alert.getService(),
                alert.getSeverity(),
                alert.getSummary()
        );
    }

    private String get(Map<String, String> map, String key) {
        if (map == null) return null;
        return map.get(key);
    }

    private String firstNonNull(String... values) {
        for (String v : values) {
            if (v != null && !v.isBlank()) {
                return v;
            }
        }
        return null;
    }
}
