package io.viana.monitor_state_manager.dto;

import io.viana.monitor_state_manager.model.ConsumerStatus;
import lombok.Data;

import java.time.Instant;

@Data
public class ConsumerPresenceDto {

    /**
     * ID do consumer group monitorado.
     */
    private String groupId;

    /**
     * Quantidade de membros ativos no grupo.
     * Usamos Integer para suportar ausência (null) se necessário.
     */
    private Integer members;

    /**
     * Status operacional do consumer group.
     */
    private ConsumerStatus status;

    /**
     * Momento da última verificação de presença.
     */
    private Instant timestamp;
}
