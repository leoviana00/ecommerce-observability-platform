package io.viana.monitor_state_manager.dto;

import lombok.Data;

@Data
public class HealthStateDto {
    private String service;
    private String status;
    private long responseTimeMs;
    private String timestamp;
}
