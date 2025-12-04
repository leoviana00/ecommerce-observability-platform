package io.viana.monitor_health_service.scheduler;

import io.micrometer.core.instrument.MeterRegistry;
import io.viana.monitor_health_service.config.MonitorConfig;
import io.viana.monitor_health_service.dto.HealthTarget;
import io.viana.monitor_health_service.model.HealthStatus;
import io.viana.monitor_health_service.service.HealthCheckService;
import io.viana.monitor_health_service.service.HealthTargetsRegistry;
import io.viana.monitor_health_service.service.StateManagerClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@RequiredArgsConstructor
public class HealthCheckScheduler {

    private final MonitorConfig monitorConfig;
    private final HealthTargetsRegistry registry;
    private final HealthCheckService healthCheckService;
    private final StateManagerClient stateManagerClient;
    private final TaskExecutor healthCheckExecutor;
    private final MeterRegistry meterRegistry;

    /**
     * Roda periodicamente os health checks para todos os targets habilitados.
     */
    @Scheduled(fixedDelayString = "#{${monitor.health-check-interval-seconds:30} * 1000}")
    public void runHealthChecks() {

        List<HealthTarget> targets = registry.getAll();

        if (targets.isEmpty()) {
            log.warn("No health targets configured; skipping health checks");
            return;
        }

        // filtra apenas targets habilitados
        List<HealthTarget> activeTargets = targets.stream()
                .filter(HealthTarget::enabledSafe)
                .toList();

        if (activeTargets.isEmpty()) {
            log.warn("No active targets enabled; skipping health checks");
            return;
        }

        log.info("Running health checks for {} active targets", activeTargets.size());

        Instant start = Instant.now();
        long startNs = System.nanoTime();

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        List<CompletableFuture<Void>> futures = activeTargets.stream()
                .map(target -> CompletableFuture.runAsync(() -> {

                    try {
                        var result = healthCheckService.checkTarget(target);

                        if (result.getStatus() == HealthStatus.UP) {
                            successCount.incrementAndGet();
                        } else {
                            failureCount.incrementAndGet();
                        }

                        // enviar para o state-manager SEM depender do retorno
                        stateManagerClient.sendHealthState(result);

                    } catch (Exception e) {
                        failureCount.incrementAndGet();
                        log.error("Unexpected error during check for target {}", target.getName(), e);
                    }

                }, healthCheckExecutor))
                .toList();

        // Aguarda finalização, mas sem bloquear agressivamente o agendador
        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new))
                .whenComplete((v, ex) -> {

                    long elapsedMs = (System.nanoTime() - startNs) / 1_000_000L;

                    if (ex != null) {
                        log.error("Batch health check completed with error", ex);
                    }

                    log.info("Health checks batch completed: total={}, ok={}, fail={}, duration={}ms",
                            activeTargets.size(),
                            successCount.get(),
                            failureCount.get(),
                            elapsedMs
                    );

                    // métricas de alto nível
                    meterRegistry.counter("health_batch_total").increment();
                    meterRegistry.counter("health_batch_success_total").increment(successCount.get());
                    meterRegistry.counter("health_batch_failure_total").increment(failureCount.get());

                    meterRegistry.timer("health_batch_duration")
                            .record(java.time.Duration.ofMillis(elapsedMs));
                });
    }
}
