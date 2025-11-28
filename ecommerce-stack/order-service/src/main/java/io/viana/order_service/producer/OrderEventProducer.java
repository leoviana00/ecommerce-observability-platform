package io.viana.order_service.producer;

import io.viana.order_service.dto.events.OrderCreatedEvent; // DTO para evento de pedido criado
import io.viana.order_service.dto.events.OrderPaidEvent; // DTO para evento de pedido pago
import lombok.RequiredArgsConstructor; // Injeta o KafkaTemplate
import lombok.extern.slf4j.Slf4j; // Habilita o logging
import org.springframework.kafka.core.KafkaTemplate; // Classe central para enviar mensagens Kafka
import org.springframework.stereotype.Component; // Marca a classe como um componente Spring

/**
 * Componente responsável por produzir (enviar) eventos importantes
 * do ciclo de vida do pedido para o Kafka.
 *
 * É essencial para iniciar a transação distribuída (SAGA) e notificar
 * serviços downstream sobre o progresso do pedido.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventProducer {

    /**
     * KafkaTemplate tipado com Object para permitir envio de múltiplos tipos
     * de eventos (OrderCreatedEvent / OrderPaidEvent).
     *
     * O JsonSerializer do Spring resolve automaticamente o tipo pelo payload.
     */
    private final KafkaTemplate<String, Object> kafkaTemplate;

    // Nomes dos tópicos de destino
    private static final String TOPIC_ORDER_CREATED = "order-created"; // Tópico para iniciar o SAGA
    private static final String TOPIC_ORDER_PAID = "order-paid"; // Tópico para notificar a conclusão do pagamento

    /**
     * Evento emitido logo após o pedido ser criado no banco de dados.
     * Este evento inicia o fluxo de reserva de estoque e pagamento.
     *
     * @param event O DTO OrderCreatedEvent com os detalhes do pedido.
     */
    public void sendOrderCreated(OrderCreatedEvent event) {
        // Envia a mensagem. O OrderId é usado como chave para garantir ordenação ou partição consistente.
        kafkaTemplate.send(TOPIC_ORDER_CREATED, String.valueOf(event.getOrderId()), event);
        log.info("📤 Evento enviado: topic={} event={}", TOPIC_ORDER_CREATED, event);
    }

    /**
     * Evento emitido quando o pagamento é confirmado com sucesso (após consumir payment-processed).
     * Este evento é o gatilho para a próxima fase do fulfillment (envio).
     *
     * @param event O DTO OrderPaidEvent com os detalhes do pagamento.
     */
    public void sendOrderPaid(OrderPaidEvent event) {
        // Envia a mensagem. OrderId é usado como chave.
        kafkaTemplate.send(TOPIC_ORDER_PAID, String.valueOf(event.getOrderId()), event);
        log.info("📤 Evento enviado: topic={} event={}", TOPIC_ORDER_PAID, event);
    }
}