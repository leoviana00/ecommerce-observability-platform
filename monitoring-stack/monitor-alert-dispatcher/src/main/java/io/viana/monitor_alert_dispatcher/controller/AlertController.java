package io.viana.monitor_alert_dispatcher.controller;

import io.viana.monitor_alert_dispatcher.dto.AlertmanagerWebhookPayload;
import io.viana.monitor_alert_dispatcher.service.AlertDispatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/alerts")
@RequiredArgsConstructor
@Slf4j
public class AlertController {

    private final AlertDispatchService alertDispatchService;

    @PostMapping
    public ResponseEntity<Void> receiveAlert(@RequestBody AlertmanagerWebhookPayload payload) {
        log.debug("Received Alertmanager webhook with status={}", payload.getStatus());
        alertDispatchService.processAlertmanagerPayload(payload);
        return ResponseEntity.accepted().build();
    }
}
