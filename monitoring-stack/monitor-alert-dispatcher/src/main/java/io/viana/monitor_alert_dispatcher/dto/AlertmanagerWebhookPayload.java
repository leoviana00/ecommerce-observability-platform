package io.viana.monitor_alert_dispatcher.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class AlertmanagerWebhookPayload {

    private String status;
    private Map<String, String> commonLabels;
    private Map<String, String> commonAnnotations;
    private List<AlertmanagerAlert> alerts;
}
