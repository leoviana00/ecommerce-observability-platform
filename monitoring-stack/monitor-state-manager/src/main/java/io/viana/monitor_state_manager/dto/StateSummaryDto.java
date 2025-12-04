package io.viana.monitor_state_manager.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.TreeMap;

@Data
@Builder
public class StateSummaryDto {

    private Map<String, Object> health;
    private Map<String, Object> lag;
    private Map<String, Object> consumer;

    public static StateSummaryDto from(Map<String, String> raw) {
        Map<String, Object> health = new TreeMap<>();
        Map<String, Object> lag = new TreeMap<>();
        Map<String, Object> consumer = new TreeMap<>();

        raw.forEach((k, v) -> {
            if (k.startsWith("health:")) {
                health.put(k.substring(7), v);
            } else if (k.startsWith("lag:")) {
                lag.put(k.substring(4), v);
            } else if (k.startsWith("consumer:")) {
                consumer.put(k.substring(9), v);
            }
        });

        return StateSummaryDto.builder()
                .health(health)
                .lag(lag)
                .consumer(consumer)
                .build();
    }
}
