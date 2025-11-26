## 🧠 1. Estrutura Pensada:

1. Gteway Ecommerce
    - ecommerce-gateway
2. 📦 Product Service
    - product-service
3. 🏬 Inventory Service
    - invntory-api
    - invntory-consumer
4. 🛒 Cart Service
    - cart-service
5. 📑 Order Service
    - order-api
    - order-aconsumer
    - order-wrkflow
6. 💳 Payment Service
    - payment-api
    - payment-consumer
7. 📨 Notification Service
    - notification-dispatcher


> [!NOTE]
> 📝 Visão orientada a eventos + API + consumidores.
> 📝 Cuidado para não misturar tudo dentro de um único serviço.

## 🚨 2. O problema

- Inicialmente foi pensado em separar os serviços API dos consumidores:

- Exemplo:
  - payment-api
  - payment-consumer

> [!CAUTION]
> 🛑 Pesquisando um pouco, vi que isso funciona, mas não é o padrão mais comum em microserviços modernos.

> [!TIP]
> 🤔 Por quê?
>  - Porque isso cria 2 deployments por bounded context, o que: duplica build, config e CI/CD
>  - depende de comunicação via banco ou Kafka entre dois “sub-serviços”
>  - não traz ganhos reais

> [!TIP]
> 💡 O padrão recomendado:
>  - Cada domínio possuir um único microserviço, contendo:
>  - Camada API (controllers REST)
>  - Camada consumer Kafka
>  - Camada producer Kafka
>  - Camada de lógica de domínio
>  - Infra (config, security, actuator, metrics)

- Assim tudo fica autônomo, escalável individualmente e consistente.

## 🧱 3. Estrutura Repensada para a stack de eCommerce

- Estrutura final (limpa e escalável)

```bash
ecommerce-stack/
│
├── ecommerce-gateway/
│
├── product-service/
│   ├── src/main/java/.../controller
│   ├── src/main/java/.../consumer
│   ├── src/main/java/.../producer
│   ├── src/main/java/.../service
│   ├── src/main/java/.../model
│   ├── src/main/java/.../config
│
├── inventory-service/
│   ├── controller
│   ├── consumer
│   ├── producer
│   ├── domain
│
├── cart-service/
│
├── order-service/
│   ├── controller (cria pedido)
│   ├── consumer (order events)
│   ├── workflow (saga/orquestração opcional)
│
├── payment-service/
│
└── notification-service/
```

## 🟦 4. Processo de transformação da estrutura

Mapeamento do que foi pensado inicialmente e → e como fica na estrutura pensada agora.


🔵 product-service/

```console
product-service/
  /controller
  /consumer
  /producer
  /service
  /model
  /config
```


🟣 inventory-api + inventory-consumer

- Viram um único serviço:

```console
/
inventory-service  controller/
  consumer/
  producer/
  service/
  model/
  config/
```

🟡 order-api + order-consumer + order-workflow

- Tudo dentro de:
```console
order-service/
  controller/
  consumer/
  workflow/  ---> aqui entra orquestração
```

🟢 payment-api + payment-consumer

- Vira:
```console
payment-service/
```

Com:

- controller: iniciar pagamento
- consumer: processar eventos
- producer: eventos de pagamento aprovado/negado

🟠 notification-dispatcher

- Vira:
```console
notification-service/
```

Com:

- consumer: payment-approved, order-created
- producer: customer-notified, vendor-notified
- adapters: email, webhook, telegram, sms, firebase (futuro)

## 🏗️ 5. Estrutura POR DENTRO de cada serviço

- Para garantir padronização entre os 7 microserviços:

```java
src/main/java/com/seuprojeto/
  controller/
  consumer/
  producer/
  service/
  domain/   (opcional)
  dto/
  model/
  config/
```

## 🚀 6. Benefícios imediatos dessa organização

```console
✔ Cada serviço é autônomo
✔ Build independente por microserviço
✔ CI/CD mais simples
✔ Kafka consumers e API REST no mesmo bounded context
✔ Ideal para SAGA / choreography
✔ Facilita testes (Testcontainers por serviço)
✔ Mais escalável
```

## 🧩 7. RESUMO Ecommerce Stack:

- Ecommerce Stack:

1. 🌐 Gteway Ecommerce [`ecommerce-gateway`]
2. 📦 Product Service [`product-service`]
3. 🏬 Inventory Service[`invntory-service`]
4. 🛒 Cart Service [`cart-service`]
5. 📑 Order Service [`order-service`]
6. 💳 Payment Service [`payment-service`]
7. 📨 Notification Service [`notification-service`]

## 📊 8. RESUMO Monitoring Stack

- Monitoring Stack (Pensar mais na frente)

1. 🩺 monitor-health-service
2. ⏱️ monitor-lag-service
3. 👀 monitor-consumer-presence-service
4. 🧠 monitor-state-manager
5. 🚨 monitor-alert-dispatcher