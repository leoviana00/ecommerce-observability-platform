## 📘 Planejamento Básico do Projeto

eCommerce Event-Driven + Observability Mesh

Planejamento macro do projeto, seus componentes fundamentais, o fluxo de domínio, padrões arquiteturais e a organização da plataforma.
Ele serve como referência inicial para desenvolvimento, expansão e governança técnica.

## 1. 🎯 Objetivo Geral

Construir um ecossistema completo de microserviços simulando uma plataforma de eCommerce, baseado em:

  - Arquitetura event-driven usando Apache Kafka
  - Microserviços independentes e escaláveis
  - Observabilidade distribuída com uma Monitoring Mesh especializada
  - Padrões de código consistentes via SkyFolder (boilerplate oficial)
  - Deploy modular e autonomia por bounded context

O objetivo é permitir:

  - Experimentação avançada com eventos
  - Testes de resiliência e observabilidade
  - Evolução incremental do domínio
  - Integração entre serviços com baixo acoplamento


## 2. 🧩 Estrutura Geral da Plataforma

A plataforma é dividida em 3 stacks principais:

### `ecommerce-stack`

Fluxos que representam o funcionamento do eCommerce:

| Serviço                | Responsabilidade Principal                                    |
|------------------------|----------------------------------------------------------------|
| 📦 product-service     | Catálogo de produtos, criação e publicação do evento `product-created`. |
| 🏬 inventory-service   | Gestão de estoque: criação inicial, movimentação e validação de disponibilidade. |
| 🛒 cart-service        | Controle do carrinho de compras: adicionar, remover e consultar itens. |
| 📑 order-service       | Criação e gerenciamento de pedidos; validações e emissão do evento `order-created`. |
| 💳 payment-service     | Processamento de pagamentos; emissão de `payment-processed` ou `payment-failed`. |
| 📨 notification-service| Envio de notificações baseadas em eventos (order, payment). |
| 🌐 ecommerce-gateway   | Roteamento unificado das requisições; entrada única da plataforma. |


### `monitoring-stack` (Observability Mesh)

Serviços especializados, cada um com uma função única:

| Serviço                             | Responsabilidade Principal                                         |
|------------------------------------|---------------------------------------------------------------------|
| 🩺 monitor-health-service          | Monitoramento de saúde dos microserviços via Actuator (ping, up/down). |
| ⏱️ monitor-lag-service             | Cálculo e monitoramento de lag em tópicos Kafka por grupo e partição. |
| 👀 monitor-consumer-presence-service | Verificação da presença/atividade de consumers Kafka (heartbeat operacional). |
| 🧠 monitor-state-manager            | Armazenamento centralizado do estado dos monitores e deduplicação de alertas. |
| 🚨 monitor-alert-dispatcher         | Despacho de alertas para canais externos (Telegram, e-mail, webhook). |


### `infra-stack`

Infraestrutura e arquitetura:

| Serviço / Componente    | Responsabilidade Principal                                             |
|--------------------------|------------------------------------------------------------------------|
| 🐳 docker-compose        | Orquestrar todos os containers locais (Kafka, Zookeeper, serviços auxiliares). |
| 📡 kafka setup           | Provisionamento do Kafka, tópicos, configurações de brokers e ACLs.   |
| 📊 observability stack   | Stack de monitoramento: Prometheus, Grafana, Loki, Tempo, exporters e dashboards. |
| 📚 documentação          | Guia técnico, diagramas C4, instruções de setup, manuais e padrões arquiteturais. |



## 3. 🔹 Fluxo Básico do Domínio (Happy Path)

Um cliente realiza um pedido seguindo o fluxo:

  1. Criação do produto

    - product-service
    - Emite evento `product-created`

  2. Criação do estoque inicial

    - inventory-service consome `product-created`
    - Inicializa o estoque

  3. Cliente adiciona produtos no carrinho

    - cart-service
    - Pode consultar `inventory-service`

  4. Finaliza o pedido

    - order-service
    - Valida estoque
    - Decrementa estoque via evento
    - Emite `order-created`

  5. Processamento do pagamento

    - payment-service
    - Emite `payment-processed` ou `payment-failed`

  6. Notificações

    - notification-service consome:
    - `order-created`
    - `payment-processed`
    - `payment-failed`

## 4. 🏗 Padrões Arquiteturais Aplicados

A plataforma segue os seguintes princípios:

| Padrão / Princípio                            | Descrição                                                         |
| --------------------------------------------- | ----------------------------------------------------------------- |
| ✔ **Event-Driven Architecture**               | Comunicação assíncrona via eventos Kafka entre todos os serviços. |
| ✔ **Single Bounded Context por Microserviço** | Cada serviço possui API, lógica, storage e integração próprios.   |
| ✔ **Observabilidade Nativa**                  | Actuator habilitado por padrão em todos os serviços.              |
| ✔ **Padronização rígida de diretórios**       | SkyFolder garante uniformidade de código e estrutura.             |
| ✔ **Package-safe (sem hífens)**               | `order-service` - pacote `order_service`.                         |
| ✔ **Convenção MainClass**                     | `order-service` - `OrderServiceApplication`.                      |


## 5. 📦 Dependências Padrão

Todos os serviços gerados pelo SkyFolder utilizam:

| Dependência                     | Artefato                         | Finalidade / Uso |
|----------------------------------|----------------------------------|-------------------|
| ✔ Spring Web                     | spring-boot-starter-web          | APIs REST |
| ✔ Spring Kafka                   | spring-kafka                     | Producers e Consumers Kafka |
| ✔ Spring Data JPA                | spring-boot-starter-data-jpa     | Persistência relacional |
| ✔ H2 Database (runtime)          | com.h2database:h2                | Banco local para dev/testes |
| ✔ Lombok                         | lombok                            | Redução de boilerplate |
| ✔ Spring Actuator                | spring-boot-starter-actuator     | Health, metrics, info |
| ✔ Spring Test                    | spring-boot-starter-test         | JUnit + Mockito |

## 6. 🛠 SkyFolder — Boilerplate Padrão

Gerar serviços via comando:

```bash
./generate-service.sh <service-name>
```

Estrutura criada:

```bash
controller/     → API REST  
service/        → Lógica de domínio  
producer/       → Eventos Kafka enviados  
consumer/       → Eventos Kafka recebidos  
config/         → Configurações gerais  
dto/events/     → Eventos de domínio  
model/          → Entidades JPA  
repository/     → Repositórios  
exception/      → Tratamento de erros  
```

Com:

  - pom.xml com dependências padrão
  - application.yml
  - Dockerfile
  - Script de execução local
  - Estrutura package-safe
  - Classe principal mapeada


## 7. 🧭 Roadmap Básico (Execução do Projeto)

- [x] Fase 1 — Fundamentos 
    - [x] Configuração do monorepo
    - [x] Definição da arquitetura
    - [x] Criação do SkyFolder
    - [x] Implementação do product-service
    - [x] Implementação do inventory-service
    - [x] Gateway configurado

- [ ] Ampliação do Fluxo de Negócio ~ Em andamento
    - [ ] cart-service
    - [ ] order-service
    - [ ] payment-service
    - [ ] notification-service
    - [ ] Criação dos principais eventos de domínio

- [ ] Fase 3 — Observability Mesh
    - [ ] monitor-lag-service
    - [ ] monitor-health-service
    - [ ] monitor-consumer-presence
    - [ ] monitor-state-manager
    - [ ] monitor-alert-dispatcher

- [ ] Fase 4 — Melhoria Contínua
    - [ ] Testcontainers por serviço
    - [ ] Automação CI/CD
    - [ ] Métricas específicas Kafka
    - [ ] Painéis Grafana
    - [ ] Documentação