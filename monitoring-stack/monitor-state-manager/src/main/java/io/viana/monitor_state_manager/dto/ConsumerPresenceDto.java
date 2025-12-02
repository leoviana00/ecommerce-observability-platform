package io.viana.monitor_state_manager.dto;

import lombok.Data;

@Data
public class ConsumerPresenceDto {
    private String groupId;
    private int members;
    private String status;
    private String timestamp;
}
