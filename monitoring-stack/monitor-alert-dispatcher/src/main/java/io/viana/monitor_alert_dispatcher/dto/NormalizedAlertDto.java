package io.viana.monitor_alert_dispatcher.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NormalizedAlertDto {

    private String status;
    private String alertName;
    private String service;
    private String severity;
    private String summary;
}
