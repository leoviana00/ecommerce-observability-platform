package io.viana.monitor_health_service.dto;

import io.viana.monitor_health_service.model.HealthStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class HealthStateDto {

    /**
     * Nome lógico do serviço monitorado (ex: product-service).
     */
    private String service;

    /**
     * Status final da checagem (UP/DOWN/DEGRADED/UNKNOWN).
     */
    private HealthStatus status;

    /**
     * Tempo total de resposta em milissegundos.
     */
    private long responseTimeMs;

    /**
     * Momento em que a checagem foi executada.
     */
    private Instant timestamp;

    /**
     * Motivo do DOWN/DEGRADED, se houver.
     */
    private String error;
}
