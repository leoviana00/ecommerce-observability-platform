## 🟩 2. inventory-service

## 🎯 Objetivo

Gerenciar estoque, informar disponibilidade e atualizar quantidades em processos de ordem.

## 🧩 Funcionalidades

- Consulta de estoque
- Atualização (reserva / baixa futura)
- Comunicação com order-service (no futuro)

## ⚙ Rotas

```bash
GET /inventory/{productId}
PUT /inventory/{productId}/reserve
PUT /inventory/{productId}/release
```
