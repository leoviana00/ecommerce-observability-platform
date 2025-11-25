## 🟧 3. cart-service

## 🎯 Objetivo

Gerenciar carrinho de compras por usuário.

## 🧩 Funcionalidades

- Adicionar itens
- Remover itens
- Listar carrinho
- Persistência em in-memory (fase 1)
- Futuro: Redis

## ⚙ Rotas

```bash
GET /cart/{userId}
POST /cart/{userId}/items
DELETE /cart/{userId}/items/{itemId}
```

