// monitor-health-service e monitor-state-manager
package io.viana.monitor_health_service.dto; // ou io.viana.monitor_state_manager.dto

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HealthStateDto {
    private String service;        // ex: product-service
    private String status;         // UP / DOWN / DEGRADED (se quiser)
    private long   responseTimeMs; // latência
    private String timestamp;      // ISO-8601, ex: 2025-12-02T18:40:00Z
    private String error;          // opcional, motivo do DOWN
}
