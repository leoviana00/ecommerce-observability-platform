package io.viana.monitor_state_manager.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.viana.monitor_state_manager.dto.ConsumerPresenceDto;
import io.viana.monitor_state_manager.dto.HealthStateDto;
import io.viana.monitor_state_manager.dto.LagStateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class StateService {

    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper; // Spring injeta automaticamente

    private HashOperations<String, String, String> ops() {
        return redis.opsForHash();
    }

    public void processHealth(HealthStateDto dto) {
        processState("health:" + dto.getService(), dto);
    }

    public void processLag(LagStateDto dto) {
        String key = "lag:" + dto.getGroupId() + ":" + dto.getPartition();
        processState(key, dto);
    }

    public void processConsumer(ConsumerPresenceDto dto) {
        String key = "consumer:" + dto.getGroupId();
        processState(key, dto);
    }

    private void processState(String key, Object dto) {
        String previous = ops().get("states", key);
        String current = toJson(dto);

        if (!current.equals(previous)) {
            log.info("State change detected [{}]", key);
            ops().put("states", key, current);

            // Futuro: disparar evento para monitor-alert-dispatcher
            // ex: alertDispatcher.notify(...)
        }
    }

    private String toJson(Object dto) {
        try {
            return objectMapper.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            log.error("Error serializing state dto", e);
            // fallback simples para não quebrar o fluxo
            return dto.toString();
        }
    }

    public Map<String, String> getCurrentState() {
        return ops().entries("states");
    }
}
