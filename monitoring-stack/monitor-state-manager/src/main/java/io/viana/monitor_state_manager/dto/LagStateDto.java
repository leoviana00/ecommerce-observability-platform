package io.viana.monitor_state_manager.dto;

import lombok.Data;

@Data
public class LagStateDto {
    private String groupId;
    private String topic;
    private int partition;
    private long lag;
    private String timestamp;
}
