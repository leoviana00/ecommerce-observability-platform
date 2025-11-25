## 🟥 4. order-service

## 🎯 Objetivo

Criar ordens, processar pedidos e coordenar etapas com payment e inventory.

## 🧩 Funcionalidades

- Criar ordem
- Consultar ordem
- Enviar eventos futuros (Kafka)
- Integração modular com payment (fase 2)

## ⚙ Rotas

```bash
POST /orders
GET /orders/{orderId}
```
