package io.viana.monitor_alert_dispatcher.dto;

import lombok.Data;

import java.time.Instant;
import java.util.Map;

@Data
public class AlertmanagerAlert {
    private Map<String, String> labels;
    private Map<String, String> annotations;
    private Instant startsAt;
    private Instant endsAt;
    private String generatorURL;
    private String fingerprint;
}
