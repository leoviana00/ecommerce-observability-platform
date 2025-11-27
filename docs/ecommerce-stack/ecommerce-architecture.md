## 🟨 3. ecommerce-architecture

## 🎯 Objetivo

Mapear arquitetura e documentação oficial.

  - Conteúdo:
    - Diagramas 
    - Arquitetura lógica
    - Fluxos entre serviços
    - Estilos de comunicação

## 1️⃣ Diagrama da Arquitetura de Microserviços

```console
                          +--------------------+
                          |  ecommerce-gateway |
                          +--------------------+
                                    |
                                    v
   +-----------------+       +-----------------+       +-----------------+
   |  product-service|       |  cart-service   |       | inventory-service|
   +-----------------+       +-----------------+       +-----------------+
   | controller      |       | controller      |       | controller      |
   | consumer        |<------|                 |       | consumer        |<------+
   | producer        |       | producer        |       | producer        |       |
   | service         |       | service         |       | service         |       |
   +-----------------+       +-----------------+       +-----------------+       |
         |                         |                         |                   |
         |                         |                         |                   |
         |                         |                         |                   |
         v                         v                         v                   |
   Kafka Topics:               Kafka Topics:             Kafka Topics:           |
   - product-created           - cart-updated           - stock-updated          |
   - product-updated           - cart-cleared           - stock-decreased        |
                                                                                 |
                                                                                 |
                                                                     +-----------------+
                                                                     | order-service   |
                                                                     +-----------------+
                                                                     | controller      |
                                                                     | consumer        |<----+
                                                                     | workflow (SAGA)|      |
                                                                     | producer        |     |
                                                                     +-----------------+     |
                                                                             |               |
                                                                             |               |
                                                                     Kafka Topics:        Kafka Topics:
                                                                     - order-created      - inventory-updated
                                                                     - order-cancelled    - payment-requested
                                                                     - order-completed    - order-updated
                                                                             |
                                                                             v
                                                                     +-----------------+
                                                                     | payment-service |
                                                                     +-----------------+
                                                                     | controller      |
                                                                     | consumer        |
                                                                     | producer        |
                                                                     +-----------------+
                                                                             |
                                                                             v
                                                                     Kafka Topics:
                                                                     - payment-approved
                                                                     - payment-rejected
                                                                             |
                                                                             v
                                                                     +---------------------+
                                                                     | notification-service|
                                                                     +---------------------+
                                                                     | consumer            |
                                                                     | producer            |
                                                                     | adapters (email,    |
                                                                     |  webhook, sms...)   |
                                                                     +---------------------+
                                                                             |
                                                                             v
                                                                     Kafka Topics:
                                                                     - customer-notified
                                                                     - vendor-notified
```

## 2️⃣ Legenda

| Cor/Elemento  | Significado                                                               |
| ------------- | ------------------------------------------------------------------------- |
| controller    | REST API do serviço                                                       |
| consumer      | Kafka consumer que processa eventos de outros serviços                    |
| producer      | Kafka producer que publica eventos                                        |
| workflow/SAGA | Orquestração de pedidos e coordenação de eventos                          |
| adapters      | Camada de integração para notificações externas (email, SMS, Telegram...) |
| Kafka Topics  | Nomes sugeridos dos tópicos para eventos relevantes                       |


## 3️⃣ Benefícios deste diagrama

- Visualiza fluxo de eventos entre serviços
- Mostra eventos Kafka e quem produz/consome
- Facilita a implementação de SAGA e observabilidade
- Serve como documentação oficial para equipe de dev e ops