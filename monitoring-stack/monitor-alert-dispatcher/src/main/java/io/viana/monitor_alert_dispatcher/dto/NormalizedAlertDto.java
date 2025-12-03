package io.viana.monitor_alert_dispatcher.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NormalizedAlertDto {

    private String status;        // firing, resolved
    private String alertName;     // ex: HighLag
    private String service;       // ex: order-service
    private String severity;      // ex: critical, warning
    private String summary;       // ex: texto amigável
    private String description;   // opcional
    private String startsAt;
    private String endsAt;
}
