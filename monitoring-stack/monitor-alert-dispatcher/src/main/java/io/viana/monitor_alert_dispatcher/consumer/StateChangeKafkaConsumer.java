package io.viana.monitor_alert_dispatcher.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.viana.monitor_alert_dispatcher.client.TelegramClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
@Slf4j
public class StateChangeKafkaConsumer {

    private final TelegramClient telegramClient;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "${state-events.topic}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void onMessage(@Payload String message, ConsumerRecord<String, String> record) {
        try {
            JsonNode node = objectMapper.readTree(message);

            String service = get(node, "service");
            String status = get(node, "status");
            String timestamp = get(node, "timestamp");

            String text = "[STATE CHANGE]\n" +
                    "Service: " + service + "\n" +
                    "Status: " + status + "\n" +
                    "Timestamp: " + timestamp;

            log.info("Kafka change: {}", text);

            telegramClient.sendMessage(text)
                    .subscribeOn(Schedulers.boundedElastic())
                    .subscribe();

        } catch (Exception e) {
            log.error("Failed: " + message, e);
        }
    }

    private String get(JsonNode node, String field) {
        JsonNode v = node.get(field);
        return v != null && !v.isNull() ? v.asText() : "";
    }
}
