package io.viana.monitor_state_manager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * Encapsula acesso ao Redis (último estado por serviço) com TTL.
 */
@Service
@RequiredArgsConstructor
public class StateStore {

    private final StringRedisTemplate redisTemplate;

    @Value("${state-manager.ttl-seconds:600}")
    private long ttlSeconds;

    private String key(String service) {
        // prefix claro, caso você queira armazenar outros tipos depois
        return "state:health:" + service;
    }

    public String load(String service) {
        return redisTemplate.opsForValue().get(key(service));
    }

    public void save(String service, String json) {
        redisTemplate
                .opsForValue()
                .set(key(service), json, Duration.ofSeconds(ttlSeconds));
    }
}
