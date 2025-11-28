package io.viana.order_service.consumer;

import io.viana.order_service.dto.events.PaymentFailedEvent; // Evento de falha no pagamento
import io.viana.order_service.dto.events.PaymentProcessedEvent; // Evento de pagamento aprovado
import io.viana.order_service.dto.events.OrderPaidEvent; // Evento a ser produzido após o pagamento
import io.viana.order_service.model.OrderEntity; // Entidade do Pedido
import io.viana.order_service.repository.OrderRepository; // Repositório para acesso ao DB
import io.viana.order_service.producer.OrderEventProducer; // Produtor para eventos downstream
import lombok.RequiredArgsConstructor; // Injeção de dependência via construtor
import lombok.extern.slf4j.Slf4j; // Ferramenta de logging
import org.springframework.kafka.annotation.KafkaListener; // Anotação para definir o método ouvinte
import org.springframework.stereotype.Component; // Marca a classe como um componente Spring

import java.time.Instant; // Para timestamp

/**
 * Consumidor Kafka responsável por processar eventos do 'payment-service'.
 * Esta classe é o ponto de controle para a transição de status do pedido
 * após a tentativa de pagamento.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderPaymentConsumer {

    // Injeção de dependências
    private final OrderRepository orderRepo;
    private final OrderEventProducer orderProducer;

    // --- Tratamento de Sucesso ---

    /**
     * Método ouvinte para o evento de pagamento aprovado.
     * Inicia o fluxo de sucesso pós-pagamento.
     */
    @KafkaListener(
            topics = "payment-processed", // Tópico de origem
            groupId = "order-service-payment", // Grupo de consumidores
            // Usa a fábrica configurada para este DTO específico
            containerFactory = "paymentProcessedListenerFactory" 
    )
    public void handlePaymentProcessed(PaymentProcessedEvent event) {
        log.info("📥 Evento recebido: payment-processed -> {}", event);

        // 1. Busca o pedido pelo ID. O ID é a chave de correlação do SAGA.
        OrderEntity order = orderRepo.findById(event.getOrderId())
                .orElse(null);

        if (order == null) {
            // Caso de Idempotência ou erro de correlação. Apenas loga e retorna.
            log.warn("⚠ Pedido inexistente para payment-processed | orderId={}", event.getOrderId());
            return;
        }

        // 2. Atualiza o status do pedido
        order.setStatus("PAID");
        order.setPaidAt(Instant.ofEpochMilli(event.getTimestamp()));
        orderRepo.save(order); // Persiste a mudança de status

        // 3. Publica evento downstream (para notificação, shipping, etc.)
        OrderPaidEvent paidEvent = OrderPaidEvent.builder()
                .orderId(order.getId())
                .userId(order.getUserId())
                .amount(order.getTotal())
                .status("PAID")
                .message("Pagamento confirmado")
                .build();

        orderProducer.sendOrderPaid(paidEvent);

        log.info("✔ Pedido atualizado -> PAID | orderId={}", order.getId());
        log.info("📤 Evento publicado: order-paid -> {}", paidEvent);
    }

    // --- Tratamento de Falha (Compensação) ---

    /**
     * Método ouvinte para o evento de pagamento falhado.
     * Inicia o fluxo de compensação e finaliza o pedido com erro.
     */
    @KafkaListener(
            topics = "payment-failed", // Tópico de origem
            groupId = "order-service-payment",
            // Usa a fábrica configurada para este DTO específico
            containerFactory = "paymentFailedListenerFactory"
    )
    public void handlePaymentFailed(PaymentFailedEvent event) {
        log.info("📥 Evento recebido: payment-failed -> {}", event);

        // 1. Busca o pedido
        OrderEntity order = orderRepo.findById(event.getOrderId())
                .orElse(null);

        if (order == null) {
            log.warn("⚠ Pedido inexistente para payment-failed | orderId={}", event.getOrderId());
            return;
        }

        // 2. Atualiza o status
        // Em um SAGA completo, este ponto acionaria a lógica de compensação,
        // como desfazer a reserva de estoque.
        order.setStatus("PAYMENT_FAILED");
        orderRepo.save(order); // Persiste a mudança de status

        log.info("❌ Pedido marcado como PAYMENT_FAILED | orderId={} | reason={}",
                 order.getId(), event.getReason());
    }
}