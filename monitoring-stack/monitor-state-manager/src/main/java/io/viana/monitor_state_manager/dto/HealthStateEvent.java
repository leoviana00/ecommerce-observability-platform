package io.viana.monitor_state_manager.dto;

import io.viana.monitor_state_manager.model.HealthStatus;
import lombok.Data;

import java.time.Instant;

/**
 * Evento recebido do monitor-health-service.
 * Health-service sempre envia o estado completo,
 * o state-manager faz dedupe semântica + TTL + repassa via Kafka.
 */
@Data
public class HealthStateEvent {

    /**
     * Nome do serviço monitorado. Obrigatório.
     */
    private String service;

    /**
     * Status operacional: UP, DOWN, UNKNOWN.
     */
    private HealthStatus status;

    /**
     * Latência em milissegundos (opcional).
     * null = desconhecido
     * 0 = fallback ou falha sem resposta
     */
    private Long latency;

    /**
     * Momento em que o health check foi realizado.
     * Opcional. Gerado automaticamente se ausente.
     */
    private Instant timestamp;
}
