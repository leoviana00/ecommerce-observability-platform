## 🏗️ Infra Stack — Documentação Oficial

A Infra Stack fornece a base operacional para todos os microserviços e para o ecossistema de observabilidade.

Ela contém os componentes essenciais para:

- Execução local da plataforma
- Provisionamento de Kafka
- Orquestração via Docker Compose
- Observabilidade (Prometheus, Grafana, Loki, Tempo)
- Governança técnica e documentação de arquitetura

Este documento descreve a estrutura, responsabilidades e instruções de uso da Infra Stack.

## 1. 🌐 Visão Geral da Infra Stack

A stack é composta pelos seguintes elementos:

| Componente          | Emoji | Responsabilidade Principal                                                  |
| ------------------- | :---: | --------------------------------------------------------------------------- |
| kafka setup         |   📡  | Configuração de Kafka (brokers, tópicos, ACLs).                             |
| docker-compose      |   🐳  | Orquestração dos serviços locais (Kafka, Zookeeper, observabilidade, etc.). |
| observability stack |   📊  | Monitoramento e métricas (Prometheus, Grafana, Loki, Tempo).                |
| documentação        |   📚  | Guias técnicos, diagramas e padrão arquitetural.                            |

## 2. 📦 Estrutura de Diretórios da Infra Stack

```bash
infra-stack/
│
├── docker/
│   ├── docker-compose.yml
│   ├── kafka/
│   │   ├── broker-config/
│   │   ├── topics/
│   │   └── zookeeper/
│   ├── grafana/
│   ├── prometheus/
│   └── loki-tempo/
│
├── kafka/
│   ├── topics-config/
│   ├── scripts/
│   └── README.md
│
├── observability/
│   ├── prometheus.yml
│   ├── grafana-provisioning/
│   ├── loki-config.yml
│   ├── tempo-config.yml
│   └── dashboards/
│
└── architecture/
    ├── diagrams/
    └── README.md
```

## 3. 🐳 Docker Compose — Ambiente Local

O arquivo docker-compose.yml é responsável por levantar:

    - Apache Kafka
    - Zookeeper
    - Prometheus
    - Grafana
    - Loki
    - Tempo
    - Kafka UI (opcional)
Outros serviços auxiliares

Subir o ambiente local:
```bash
cd infra-stack/docker
docker-compose up -d
```

Parar:
```bash
docker-compose down
```

Status:
```bash
docker ps
```

## 4. 📡 Kafka Setup

O Kafka é usado como backbone de eventos da plataforma.
A Infra Stack mantém dentro de kafka/:

- Configurações de brokers

Arquivos de propriedades dos brokers (se necessário).

- Scripts de criação de tópicos

Exemplo:
```bash
create-topic.sh
list-topics.sh
delete-topic.sh
```

- **Padrões de nomenclatura**
```console
product-created
order-created
payment-processed
inventory-updated
```

Criar tópicos manualmente (exemplo):
```bash
docker exec -it kafka-broker \
  kafka-topics.sh --create \
  --topic order-created \
  --bootstrap-server localhost:9092 \
  --partitions 3 \
  --replication-factor 1
```

Listar tópicos:
```bash
docker exec -it kafka-broker \
  kafka-topics.sh --list --bootstrap-server localhost:9092
```

## 5. 📊 Observability Stack

A observabilidade é composta por:
| Componente     | Descrição                                               |
| -------------- | ------------------------------------------------------- |
| **Prometheus** | Coleta métricas (Spring Boot Actuator, Kafka Exporter). |
| **Grafana**    | Dashboards e visualização.                              |
| **Loki**       | Centralização de logs distribuídos.                     |
| **Tempo**      | Rastreamento distribuído (tracing).                     |
| **Exporters**  | Kafka Exporter, Node Exporter, JMX Exporter.            |

Subir a stack completa:
```bash
cd infra-stack/docker
docker-compose up -d grafana prometheus loki tempo
```

Painéis disponíveis:
| Serviço    | URL Local                                      |
| ---------- | ---------------------------------------------- |
| Grafana    | [http://localhost:3000](http://localhost:3000) |
| Prometheus | [http://localhost:9090](http://localhost:9090) |
| Tempo      | [http://localhost:3200](http://localhost:3200) |
| Loki       | via Grafana Explore                            |

## 6. 📚 Documentação de Arquitetura

A pasta infra-stack/documentation/ contém:

- Diagramas 
    - Contexto
    - Containers
    - Components
    - Code (opcional)

- Diagramas de fluxo
    - Customer -> Order -> Payment
    - Inventory synchronization
    - Observability Mesh

- Padrões técnicos
    - Naming conventions
    - Estrutura de serviços (SkyFolder)
    - Padrões de eventos
    - Topologia Kafka

- Manuais de operação
    - Como subir a plataforma
    - Como monitorar a plataforma
    - Como criar novos serviços

## 7. 🧭 Roadmap da Infra Stack

| Fase | Item                                     | Status                    |
| ---- | ---------------------------------------- | ------------------------- |
| 1    | Docker Compose base                      | ✔ Concluído               |
| 2    | Kafka Setup                              | ✔ Concluído / Em expansão |
| 3    | Observability stack (Prometheus/Grafana) | ✔ Inicial                 |
| 4    | Loki + Tempo                             | 🔄 Em andamento           |
| 5    | Monitoramento Kafka Exporter             | 🔄 Em andamento           |
| 6    | Dashboards Grafana completos             | 🔄 Em andamento           |
| 7    | Automatização de setup (scripts)         | 🔄 Planejado              |
| 8    | Tooling para CI/CD                       | ⏳ Futuro                  |
| 9    | Infra IaC (Terraform / Ansible)          | ⏳ Futuro                  |

## 8. 🎯 Objetivo da Infra Stack

- A Infra Stack garante que:
    - Todo desenvolvedor consiga subir o ambiente completo localmente
    - Kafka esteja funcionando de forma previsível
    - Logs, métricas e traces sejam visíveis desde o primeiro commit
    - O ecossistema de serviços tenha observabilidade completa
    - A arquitetura seja documentada e compreensível