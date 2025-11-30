# 📢 notification-service

Serviço responsável por **notificar usuários e sistemas externos** após eventos críticos do fluxo e-commerce.

## 🎯 Objetivo
Consumir eventos do Kafka e disparar notificações correspondentes.

## 📡 Eventos Consumidos

| Evento | Tópico | Descrição |
|--------|--------|-----------|
| OrderPaidEvent | `order-paid` | Emitido pelo order-service após pagamento aprovado |
| PaymentFailedEvent | `payment-failed` | Emitido pelo payment-service quando um pagamento falha |

## 🧠 Responsabilidades

- Enviar notificações de pagamento aprovado
- Enviar notificações de falha no pagamento
- Registrar logs estruturados
- Base futura para envios por email, SMS, push, webhooks

## 🧩 Tecnologias
- Spring Boot 3
- Spring Kafka
- Lombok
- Actuator
