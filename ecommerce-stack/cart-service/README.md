## 🎯 1. Objetivo 

O cart-service gerencia o estado do carrinho do cliente.

Ele precisa ser:

- stateless via API
- mas stateful via storage (H2 ou Postgres em produção)
- isolado por usuário (userId)
- integrável com inventory-service
- simples e rápido, pois trata de interações em tempo real

## 📌 2. Responsabilidades 

✔ Responsabilidades principais

- Criar carrinho para um cliente (se não existir).
- Adicionar produto ao carrinho.
- Remover produto do carrinho.
- Listar o carrinho com itens e totais.
- Validar disponibilidade de estoque chamando inventory-service.
- Preparar o pedido (order-request DTO).
- Em um futuro próximo: emitir evento cart-checkedout.

❌ O que NÃO é responsabilidade do cart-service

- Não decrementar estoque
- Não criar pedido
- Não orquestrar pagamento
- Não enviar notificações

🏗️ 3. Estrutura interna (padrão SkyFolder)

Quando criarmos o projeto:
```bash
cart-service/
  controller/
  service/
  repository/
  model/
  config/
  exception/
  dto/
  dto/events/
  producer/
  consumer/ (provavelmente vazio neste serviço)
```

## 🔶 4. Modelagem de domínio

Simples e funcional:

📄 Entidades

1. `CartEntity`
```bash
id
userId
createdAt
updatedAt
```

2. `CartItemEntity`
```bash
id
cartId (fk)
productId
productName (snapshot opcional)
unitPrice
quantity
```

## 🔄 5. Fluxos básicos 

✔ 1. Criar carrinho (lazy)

- Se o usuário chamar /cart/{userId} e não existir: `cria automaticamente`.

✔ 2. Adicionar item ao carrinho

- Recebe: `productId`, `quantity`.
- Consulta inventory-service:
  - GET /inventory/{productId}
- Se disponível: `adiciona item`.
- Se já houver item: `incrementa quantidade`.

✔ 3. Remover item

- Remove ou decrementa.

✔ 4. Obter carrinho

- GET /cart/{userId}
- Retorna itens + total calculado.

✔ 5. Limpeza ao fechar pedido (futuro)

- Limpar carrinho após checkout.

## 💬 6. APIs 

API inicial a ser construida:

- POST /cart/{userId}/items

Adicionar item ao carrinho.

Body:
```bash
{
  "productId": 1,
  "quantity": 2
}
```

- DELETE /cart/{userId}/items/{productId}

Remover item.


- GET /cart/{userId}

Listar carrinho.

Resposta:
```bash
{
  "userId": 10,
  "items": [
    { "productId": 1, "quantity": 2, "unitPrice": 100.0 }
  ],
  "total": 200.0
}
```

## 📡 7. Integração 

Integração com o Iventory

Quando o usuário adicionar um item, chamamos:

```bash
GET http://inventory-service/inventory/{productId}
```

Validações:

  - Se não existe --> erro
  - Se stockQuantity == 0 --> indisponível
  - Se quantity solicitada > disponível --> ajustar ou bloquear

## 📎 8. Dependências

| Item                 | Necessário?                            |
| -------------------- | -------------------------------------- |
| Spring Web           | ✔                                      |
| Spring Data JPA      | ✔                                      |
| Spring Boot Actuator | ✔                                      |
| Lombok               | ✔                                      |
| H2                   | ✔                                      |
| Spring Kafka         | opcional (no futuro `cart-checkedout`) |

## 🚀 9. Primeiro passo prático

A próxima ação é gerar o esqueleto do serviço:

```bash
./generate-service.sh cart-service
```

Depois ajusto o conteúdo gerado:

```console
criar entidades

criar repositórios

criar DTOs

criar serviço

criar controller

integração HTTP com inventory-service
```
