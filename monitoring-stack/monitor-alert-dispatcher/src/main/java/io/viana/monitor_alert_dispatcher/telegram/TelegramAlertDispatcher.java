package io.viana.monitor_alert_dispatcher.telegram;

import io.viana.monitor_alert_dispatcher.client.TelegramClient;
import io.viana.monitor_alert_dispatcher.dto.NormalizedAlertDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.scheduler.Schedulers;

@Component
@RequiredArgsConstructor
public class TelegramAlertDispatcher {

    private final TelegramClient telegramClient;

    public void dispatch(NormalizedAlertDto alert) {
        String text = format(alert);

        telegramClient.sendMessage(text)
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe();
    }

    private String format(NormalizedAlertDto alert) {
        return "[PROMETHEUS ALERT]\n" +
               "Name: " + alert.getAlertName() + "\n" +
               "Status: " + alert.getStatus() + "\n" +
               "Service: " + alert.getService() + "\n" +
               "Severity: " + alert.getSeverity() + "\n" +
               "Summary: " + alert.getSummary();
    }
}
