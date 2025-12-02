package io.viana.monitor_state_manager.controller;

import io.viana.monitor_state_manager.dto.ConsumerPresenceDto;
import io.viana.monitor_state_manager.dto.HealthStateDto;
import io.viana.monitor_state_manager.dto.LagStateDto;
import io.viana.monitor_state_manager.service.StateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/state")
public class StateController {

    private final StateService stateService;

    @PostMapping("/health")
    public ResponseEntity<Void> ingestHealth(@RequestBody HealthStateDto dto) {
        stateService.processHealth(dto);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/lag")
    public ResponseEntity<Void> ingestLag(@RequestBody LagStateDto dto) {
        stateService.processLag(dto);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/consumer")
    public ResponseEntity<Void> ingestConsumer(@RequestBody ConsumerPresenceDto dto) {
        stateService.processConsumer(dto);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/summary")
    public ResponseEntity<Object> getSummary() {
        return ResponseEntity.ok(stateService.getCurrentState());
    }
}
