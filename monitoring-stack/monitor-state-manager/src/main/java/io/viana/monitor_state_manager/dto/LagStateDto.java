package io.viana.monitor_state_manager.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class LagStateDto {

    /**
     * Consumer group monitorado.
     */
    private String groupId;

    /**
     * Tópico Kafka ao qual o lag se refere.
     */
    private String topic;

    /**
     * Partição em questão.
     * Integer para permitir null em cenários especiais, se necessário.
     */
    private Integer partition;

    /**
     * Lag absoluto (mensagens em atraso).
     */
    private long lag;

    /**
     * Momento da medição do lag.
     */
    private Instant timestamp;
}
