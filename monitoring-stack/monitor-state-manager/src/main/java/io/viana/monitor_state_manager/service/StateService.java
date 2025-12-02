package io.viana.monitor_state_manager.service;

import io.viana.monitor_state_manager.dto.*;
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
        String current = dto.toString();

        if (!current.equals(previous)) {
            log.info("State change detected [{}]: {}", key, current);
            ops().put("states", key, current);

            // FUTURO: enviar evento para o monitor-alert-dispatcher
            // alertDispatcher.notify(AlertEventDto.builder()...)
        }
    }

    public Map<String, String> getCurrentState() {
        return ops().entries("states");
    }
}
