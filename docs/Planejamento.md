## ✅ Projeto: eCommerce Event-Driven + Observability Mesh

### 🎯 Objetivo

Criar um ecossistema de microserviços que simula um fluxo completo de compras, com eventos transitando pelo Kafka, e paralelamente uma “malha de observabilidade” composta por microserviços especializados em monitoramento, cada um responsável por uma dimensão específica do sistema, persistindo estados e evitando alertas duplicados.

### 🧩 1. STACK PRINCIPAL (Fluxo de Compra)

Uma arquitetura simples, modular e 100% orientada a eventos:

```console
┌────────────────────────┐
│     API Gateway        │
└──────────┬─────────────┘
           │
     (REST chamadas)
           ▼
┌────────────────────────┐
│   Product Service      │  → Consulta catálogo
└────────────────────────┘
┌────────────────────────┐
│ Inventory Service      │  → Valida e debita estoque
└────────────────────────┘
┌────────────────────────┐
│ Cart Service           │  → Monta carrinho
└────────────────────────┘
┌────────────────────────┐
│ Order Service          │  → Cria pedido / publica evento
└────────────────────────┘
┌────────────────────────┐
│ Payment Service        │  → Simula pagamento
└────────────────────────┘
┌────────────────────────┐
│ Notification Service   │  → Notifica cliente e vendor
└────────────────────────┘

```

- **Eventos no Kafka**

Tópicos pensados:

  - product-selected
  - inventory-checked
  - cart-updated
  - order-created
  - payment-approved
  - customer-notified
  - vendor-notified

Cada microserviço:

  - expõe endpoints REST
  - consome e produz mensagens
  - possui health check (Spring Boot Actuator)
  - envia métricas (Micrometer + Prometheus)

### 🧩 2. STACK DE MONITORAMENTO (Observability Mesh)

Ideia: não ter um único microserviço de monitoramento, mas sim um conjunto, cada um com uma responsabilidade específica.

```console
                         ┌──────────────────────────────┐
                         │ Monitor State Manager         │
                         │  - Persiste estados           │
                         │  - Evita alertas repetidos    │
                         │  - Publica eventos             │
                         └──────────────┬───────────────┘
                                        │(eventos)
             ┌──────────────────────────┼──────────────────────────┐
             ▼                          ▼                          ▼
┌─────────────────────┐   ┌────────────────────────┐   ┌───────────────────────────┐
│ HealthCheckService  │   │ LagMonitorService      │   │ ConsumerPresenceService   │
│ - Verifica /health  │   │ - Lê lag de grupos     │   │ - Detecta consumidores    │
└─────────────────────┘   │ - Kafka AdminClient    │   │   offline                 │
                          └────────────────────────┘   └───────────────────────────┘
                                          │
                                          ▼
                             ┌────────────────────────┐
                             │ Alert Dispatcher       │
                             │ - Telegram/E-mail/etc  │
                             └────────────────────────┘

```

## Microserviços e responsabilidades pensados

### ⭐ 1. HealthCheckMonitorService

Executa chamadas periódicas para os health checks:

  - /actuator/health
  - /actuator/metrics

Detecta:

  - API down
  - Falha em DB
  - Falha em Kafka

Publica eventos no tópico:

  - health-alert-events

### ⭐ 2. LagMonitorService

Usa `AdminClient` para:

  - listar offsets
  - calcular lag acumulado
  - identificar partições com atraso

Publica eventos no tópico:

  - lag-alert-events

### ⭐ 3. ConsumerPresenceService

Detecta:

  - consumer groups sem membros
  - consumers que sumiram (rebalanceamento mal-sucedido)

Publica eventos no tópico:

  - consumer-absence-events

### ⭐ 4. Monitor State Manager (core da inteligência do sistema)

Responsável por:

  - armazenar estados de alerta:
    - última ocorrência
    - última normalização
    - estado atual ("OK" | "WARN" | "CRITICAL")

  - evitar alertas duplicados

  - emitir evento de normalização:
    - service-back-to-normal-events

  - persistência:
    - Postgres ou Redis para TTL (ideal para estados efêmeros)

  - regra de deduplicação:
    - “não envia o mesmo alerta se já estiver ativo”
    - “envia apenas quando sair do estado OK ou voltar para OK”

### ⭐ 5. Alert Dispatcher

Recebe eventos de alerta filtrados

  - Envia via:
    - Telegram
    - Email
    - Webhook
  - Quando recebe `service-back-to-normal-events`, envia notificação de normalização.

## 🗃️ 3. Persistência

Para estados:

  - Opção A – Redis

    - TTL natural
    - operações simples
    - ótimo para estados de alerta

  - Opção B – Postgres

    - histórico de alertas
    - auditabilidade

## 🧰 4. Tecnologias e libs

| Tema                   | Tecnologia            |
| ---------------------- | --------------------- |
| Linguagem              | Java 21               |
| Build                  | Maven                 |
| Framework              | Spring Boot 3.x       |
| Configuração           | Spring Cloud Config   |
| Observabilidade        | Micrometer + Actuator |
| Exportação de métricas | Prometheus            |
| Coleta centralizada    | Grafana               |
| Mensageria             | Kafka                 |
| Persistência           | Postgres / Redis      |
| Testes                 | JUnit + Mockito       |

## 🛠️ 5. Tópicos Kafka da stack de monitoramento

```console
monitor-health-alerts
monitor-lag-alerts
monitor-consumer-presence
monitor-state-changes
monitor-normalization
```

## 🧪 6. Fluxo de Alerta (Exemplo)

Exemplo: Lag de consumo subiu muito

1. LagMonitorService detecta lag anormal
2. Publica no tópico: monitor-lag-alerts
3. MonitorStateManager:
  - verifica estado anterior (OK → CRITICAL)
  - salva estado no Redis
  - repassa evento para o AlertDispatcher
4. AlertDispatcher envia mensagem no Telegram
5. Quando lag normaliza:
  - novo evento
  - estado muda para OK
  - envia notificação de normalização

## 📦 7. Estrutura de Pastas

### 🛒 Microserviços da Stack de Vendas (Business Domain)

| Serviço                         | Nome pensado         |
| ------------------------------- | ------------------------ |
| API Gateway                     | **ecommerce-gateway**    |
| Catálogo                        | **product-service**      |
| Estoque                         | **inventory-service**    |
| Carrinho                        | **cart-service**         |
| Pedidos                         | **order-service**        |
| Pagamentos                      | **payment-service**      |
| Notificações (cliente + vendor) | **notification-service** |

### 🔎 Observability Mesh (Microserviços de Monitoramento)

| Serviço                              | Nome pensado                      |
| ------------------------------------ | ------------------------------------- |
| Monitor de Health Check              | **monitor-health-service**            |
| Monitor de Lag Kafka                 | **monitor-lag-service**               |
| Monitor de Consumers Ausentes        | **monitor-consumer-presence-service** |
| Gerenciador de Estado (deduplicador) | **monitor-state-manager**             |
| Serviço de Notificações de alerta    | **monitor-alert-dispatcher**          |


### 🧰 Infraestrutura e Suporte

| Categoria                                           | Nome pensado                          |
| --------------------------------------------------- | ----------------------------------------- |
| Configuração Spring Cloud Config                    | **config-server** ou **ecommerce-config** |
| Kafka + Docker Compose + Infra local                | **ecommerce-infra**                       |
| Observabilidade (Prometheus + Grafana + dashboards) | **ecommerce-observability-stack**         |
| Helm Charts ou Deploy K8s                           | **ecommerce-k8s-deploy**                  |


### 🧱 Repositórios de Documentação

| Propósito                         | Nome recomendado           |
| --------------------------------- | -------------------------- |
| Documentação técnica geral        | **ecommerce-docs**         |
| Diagramas (C4, fluxo, sequências) | **ecommerce-architecture** |

## 🎯 8. Sugestão final de estrutura (melhor prática)

```console

ecommerce-observability-platform/
│
├── ecommerce-stack/
│   ├── ecommerce-gateway/
│   ├── product-service/
│   ├── inventory-service/
│   ├── cart-service/
│   ├── order-service/
│   ├── payment-service/
│   └── notification-service/
│
├── monitoring-stack/
│   ├── monitor-health-service/
│   ├── monitor-lag-service/
│   ├── monitor-consumer-presence-service/
│   ├── monitor-state-manager/
│   └── monitor-alert-dispatcher/
│
├── infra-stack/
|   ├── ecommerce-infra
|   ├── ecommerce-observability-stack
|   └── ecommerce-architecture
|   
└── docs/
    ├── diagrams/s
    └── readmes/
    
```

## 🚀 9. Resultado Final

A ideia é obterno final:

✔ Uma simulação completa de eCommerce

✔ Toda a comunicação por eventos (Kafka)

✔ Microserviços independentes

✔ Uma malha de observabilidade inteligente

✔ Deduplicação de alertas

✔ Notificação e normalização

✔ Estado persistido e analisável

✔ Ideal para estudos de DevOps + SRE + Arquitetura Distribuída


