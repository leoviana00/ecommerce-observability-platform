## 🧩 1. STACK DE MONITORAMENTO (Observability Mesh)

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

## 2. Microserviços e responsabilidades pensados

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

### 🔎 Observability Mesh (Microserviços de Monitoramento)

| Serviço                              | Nome pensado                      |
| ------------------------------------ | ------------------------------------- |
| Monitor de Health Check              | **monitor-health-service**            |
| Monitor de Lag Kafka                 | **monitor-lag-service**               |
| Monitor de Consumers Ausentes        | **monitor-consumer-presence-service** |
| Gerenciador de Estado (deduplicador) | **monitor-state-manager**             |
| Serviço de Notificações de alerta    | **monitor-alert-dispatcher**          |

