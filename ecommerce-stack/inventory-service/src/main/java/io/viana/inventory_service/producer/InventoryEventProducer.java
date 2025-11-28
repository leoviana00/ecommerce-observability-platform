package io.viana.inventory_service.producer;

import io.viana.inventory_service.dto.InventoryUpdatedEvent; // DTO para evento de estoque atualizado (sucesso)
import io.viana.inventory_service.dto.InventoryReserveFailedEvent; // DTO para evento de falha na reserva (erro)

import lombok.RequiredArgsConstructor; // Gera construtor para injeção de dependência
import lombok.extern.slf4j.Slf4j; // Facilita o logging

import org.springframework.kafka.core.KafkaTemplate; // Classe central para enviar mensagens Kafka
import org.springframework.stereotype.Component; // Marca a classe como um componente Spring

/**
 * Componente responsável por produzir (enviar) eventos de alteração de estoque
 * e de falhas de reserva para tópicos do Kafka.
 * Essencial para permitir que outros microsserviços reajam ao estado do inventário.
 */
@Slf4j // Habilita o logging
@Component // Componente Spring
@RequiredArgsConstructor // Injeta o KafkaTemplate
public class InventoryEventProducer {

    // Injeção de dependência do KafkaTemplate. Usa 'Object' como tipo de valor
    // pois a classe enviará DTOs de diferentes tipos (Updated e Failed).
    private final KafkaTemplate<String, Object> kafkaTemplate;

    // Nomes dos tópicos de destino
    private static final String TOPIC_UPDATED = "inventory-updated"; // Tópico para eventos de sucesso/mudança de estoque
    private static final String TOPIC_FAILED  = "inventory-reserve-failed"; // Tópico para eventos de falha na reserva

    /**
     * Envia evento de atualização de estoque (aumento ou diminuição bem-sucedida).
     * Informa aos serviços interessados (ex: Notificação, Auditoria) sobre a mudança.
     * @param event O DTO InventoryUpdatedEvent contendo os detalhes da mudança.
     */
    public void sendInventoryUpdated(InventoryUpdatedEvent event) {

        // Envia o DTO para o tópico de atualizações.
        kafkaTemplate.send(TOPIC_UPDATED, event);

        // Loga a ação de envio.
        log.info("📤 Evento enviado para Kafka [{}]: {}", TOPIC_UPDATED, event);
    }

    /**
     * Envia evento de falha na reserva de estoque (por estoque insuficiente).
     * Este evento é crucial para o padrão SAGA, permitindo que o serviço solicitante
     * (ex: Order Service) reverta sua transação.
     * @param event O DTO InventoryReserveFailedEvent contendo os detalhes da falha.
     */
    public void sendReserveFailed(InventoryReserveFailedEvent event) {

        // Envia o DTO para o tópico de falhas.
        kafkaTemplate.send(TOPIC_FAILED, event);

        // Loga a falha com nível de alerta (WARN).
        log.warn("📤 Evento enviado para Kafka [{}]: {}", TOPIC_FAILED, event);
    }
}