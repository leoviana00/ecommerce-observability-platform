package io.viana.monitor_state_manager.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.viana.monitor_state_manager.dto.HealthStateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Orquestra:
 *  - Dedupe semântica (status)
 *  - TTL / cache de último estado em Redis
 *  - Emissão de evento em Kafka apenas em caso de mudança
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StateService {

    private final StateStore stateStore;
    private final KafkaStatePublisher kafkaStatePublisher;
    private final SemanticComparator semanticComparator;
    private final ObjectMapper objectMapper;

    public void handle(HealthStateEvent event) throws JsonProcessingException {

        String service = event.getService();
        String newJson = objectMapper.writeValueAsString(event);

        String oldJson = stateStore.load(service);

        boolean changed = semanticComparator.changed(oldJson, newJson);

        if (!changed) {
            if (log.isDebugEnabled()) {
                log.debug("No semantic change for service={} status={}", service, event.getStatus());
            }
            return;
        }

        stateStore.save(service, newJson);
        kafkaStatePublisher.publish(service, newJson);

        log.info("State change detected [service={}] => {}", service, event.getStatus());
        if (log.isDebugEnabled()) {
            log.debug("New state for {}: {}", service, newJson);
        }
    }
}
