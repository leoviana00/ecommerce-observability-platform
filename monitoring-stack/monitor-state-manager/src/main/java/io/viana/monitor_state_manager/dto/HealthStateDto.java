package io.viana.monitor_state_manager.dto;

import io.viana.monitor_state_manager.model.HealthStatus;
import lombok.Data;

import java.time.Instant;

@Data
public class HealthStateDto {

    /**
     * Nome lógico do serviço (ex: order-service).
     */
    private String service;

    /**
     * Status de saúde do serviço.
     */
    private HealthStatus status;

    /**
     * Tempo de resposta em milissegundos.
     */
    private long responseTimeMs;

    /**
     * Momento em que o health foi medido.
     */
    private Instant timestamp;
}
