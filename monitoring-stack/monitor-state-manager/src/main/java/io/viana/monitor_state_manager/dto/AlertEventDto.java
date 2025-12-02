package io.viana.monitor_state_manager.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AlertEventDto {
    private String type;
    private Object previous;
    private Object current;
    private String timestamp;
}
