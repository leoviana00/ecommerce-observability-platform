package io.viana.monitor_state_manager.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.viana.monitor_state_manager.dto.HealthStateEvent;
import io.viana.monitor_state_manager.service.StateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * API de entrada para o monitor-health-service.
 *
 * Health-service envia SEMPRE o estado completo de todos os serviços monitorados.
 * O state-manager faz:
 *  - dedupe semântico
 *  - persistência TTL
 *  - publicação Kafka (quando houver mudança)
 *
 * Endpoint:
 *     POST /state/health
 * Payload:
 *     HealthStateEvent
 *
 * Retorno:
 *     202 ACCEPTED sempre que recebido, mesmo que dedupe impeça update.
 *
 * Erros:
 *     - 400 Bad Request se payload inválido
 *     - 500 Internal Server Error se falha inesperada
 */

@RestController
@RequestMapping("/state")
@RequiredArgsConstructor
@Slf4j
public class StateController {

    private final StateService stateService;

    @PostMapping("/health")
    public ResponseEntity<Void> ingest(@RequestBody HealthStateEvent event) {

        // ===== VALIDATION: PAYLOAD =====
        if (event == null) {
            log.warn("Received null payload on /state/health");
            return ResponseEntity.badRequest().build();
        }

        if (event.getService() == null || event.getService().isBlank()) {
            log.warn("Invalid event - missing service field: {}", event);
            return ResponseEntity.badRequest().build();
        }

        // ===== PROCESSING =====
        try {
            if (log.isDebugEnabled()) {
                log.debug("Received health state event: service={} status={} latency={}",
                        event.getService(),
                        event.getStatus(),
                        event.getLatency()
                );
            }

            stateService.handle(event);

            // Sempre 202 mesmo se dedupe
            return ResponseEntity.accepted().build();

        } catch (JsonProcessingException e) {
            // falha ao serializar para Redis/Kafka
            log.error("JSON error while processing event: {}", event, e);
            return ResponseEntity.internalServerError().build();

        } catch (Exception e) {
            // falha inesperada
            log.error("Unexpected error while processing event: {}", event, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
