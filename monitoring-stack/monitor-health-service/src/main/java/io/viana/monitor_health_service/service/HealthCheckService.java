package io.viana.monitor_health_service.service;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.viana.monitor_health_service.dto.HealthStateDto;
import io.viana.monitor_health_service.dto.HealthTarget;
import io.viana.monitor_health_service.model.HealthStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.concurrent.Callable;

@Slf4j
@Service
@RequiredArgsConstructor
public class HealthCheckService {

    private final RestTemplate healthRestTemplate;
    private final MeterRegistry meterRegistry;
    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final RetryRegistry retryRegistry;

    public HealthStateDto checkTarget(HealthTarget target) {

        String targetName = target.getName();

        // Registries criam ou retornam instâncias já existentes
        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker("health-check-" + targetName);
        Retry retry = retryRegistry.retry("health-check-" + targetName);

        // Função base
        Callable<HealthStateDto> callable = () -> doSingleCheck(target);

        // Encadeia Resilience4J manualmente
        Callable<HealthStateDto> decorated =
                CircuitBreaker.decorateCallable(cb,
                        Retry.decorateCallable(retry, callable)
                );

        // Timer micrometer
        Timer.Sample sample = Timer.start(meterRegistry);

        HealthStateDto result;

        try {
            result = decorated.call();
        } catch (Exception e) {
            log.error("Health check failed after retries for target {}", targetName, e);

            result = HealthStateDto.builder()
                    .service(targetName)
                    .status(HealthStatus.DOWN)
                    .responseTimeMs(0L)
                    .timestamp(Instant.now())
                    .error("Failed after retry: " + e.getMessage())
                    .build();
        }

        // Métricas
        recordMetrics(targetName, result, sample);

        log.debug("Health check result for {}: status={}, latency={}ms",
                targetName, result.getStatus(), result.getResponseTimeMs());

        return result;
    }

    private void recordMetrics(String target, HealthStateDto result, Timer.Sample sample) {

        String status = result.getStatus().name();

        // Contador total
        Counter.builder("health_checks_total")
                .tag("target", target)
                .tag("status", status)
                .register(meterRegistry)
                .increment();

        // Contador de falhas
        if (result.getStatus() != HealthStatus.UP) {
            Counter.builder("health_checks_failed_total")
                    .tag("target", target)
                    .register(meterRegistry)
                    .increment();
        }

        // Latência
        sample.stop(
                Timer.builder("health_check_duration")
                        .tag("target", target)
                        .tag("status", status)
                        .register(meterRegistry)
        );
    }

    private HealthStateDto doSingleCheck(HealthTarget target) {

        String targetName = target.getName();
        String url = target.getUrl();

        Instant start = Instant.now();
        long startNs = System.nanoTime();

        try {
            ResponseEntity<String> response =
                    healthRestTemplate.getForEntity(url, String.class);

            long elapsedMs = (System.nanoTime() - startNs) / 1_000_000L;

            if (response.getStatusCode().is2xxSuccessful()) {

                return HealthStateDto.builder()
                        .service(targetName)
                        .status(HealthStatus.UP)
                        .responseTimeMs(elapsedMs)
                        .timestamp(start)
                        .error(null)
                        .build();

            } else {

                return HealthStateDto.builder()
                        .service(targetName)
                        .status(HealthStatus.DOWN)
                        .responseTimeMs(elapsedMs)
                        .timestamp(start)
                        .error("Non-2xx status: " + response.getStatusCode())
                        .build();
            }

        } catch (Exception e) {

            long elapsedMs = (System.nanoTime() - startNs) / 1_000_000L;

            log.warn("Exception during health check for {} at {}: {}", targetName, url, e.getMessage());

            return HealthStateDto.builder()
                    .service(targetName)
                    .status(HealthStatus.DOWN)
                    .responseTimeMs(elapsedMs)
                    .timestamp(start)
                    .error(e.getClass().getSimpleName() + ": " + e.getMessage())
                    .build();
        }
    }
}
