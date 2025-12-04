package io.viana.monitor_state_manager.dto;

import io.viana.monitor_state_manager.model.Severity;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class AlertEventDto<T> {

    /**
     * Identificador único do alerta (ex: key + epochMillis).
     */
    private String id;

    /**
     * Tipo de estado que gerou o alerta (HEALTH, LAG, CONSUMER).
     */
    private String type;

    /**
     * Origem lógica do alerta (ex: health:order-service).
     */
    private String source;

    /**
     * Severidade do alerta (INFO, WARNING, CRITICAL).
     * Por enquanto está fixo em INFO; pode ser evoluído com regras específicas.
     */
    private Severity severity;

    /**
     * Representação anterior do estado (JSON ou objeto tipado, dependendo do uso).
     */
    private T previous;

    /**
     * Novo estado após a mudança.
     */
    private T current;

    /**
     * Momento em que a transição foi detectada.
     */
    private Instant timestamp;
}
