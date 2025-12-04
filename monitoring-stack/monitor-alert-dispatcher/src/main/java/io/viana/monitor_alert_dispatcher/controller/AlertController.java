package io.viana.monitor_alert_dispatcher.controller;

import io.viana.monitor_alert_dispatcher.dto.AlertmanagerWebhookPayload;
import io.viana.monitor_alert_dispatcher.service.AlertDispatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertDispatchService alertDispatchService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public void receive(@RequestBody AlertmanagerWebhookPayload payload) {
        alertDispatchService.processAlertmanagerPayload(payload);
    }
}
