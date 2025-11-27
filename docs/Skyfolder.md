## 📘 SkyFolder – Padrão de Microserviços para o projeto eCommerce-Observability

## 📌 Visão Geral

O SkyFolder é o boilerplate oficial utilizado para gerar novos microserviços dentro da plataforma eCommerce Event-Driven + Observability Mesh.
Ele garante que todos os serviços seguem os mesmos padrões, promovendo:

  - Consistência arquitetural
  - Manutenção simplificada
  - Menos erros operacionais
  - Adoção natural de boas práticas de DDD + event-driven
  - Padrões Kafka, REST e Observabilidade integrados

Todos os serviços gerados a partir daqui já nascem prontos para:

  - APIs REST
  - Kafka Producer/Consumer
  - DTOs de eventos padronizados
  - Configurações padrão Spring Boot 3.2
  - Actuator / Healthchecks
  - JPA + H2 default
  - Empacotamento Docker
  - Execução local

## 🚀 Como gerar um novo microserviço

Script: [generate-service.sh](../ecommerce-stack/generate-service.sh)

Execute no terminal: 

```bash
cd ecommerce-stack 
./generate-service.sh <nome-do-servico>
```

Exemplos:

```bash
./generate-service.sh payment-service
./generate-service.sh cart-service
./generate-service.sh monitor-lag-service
```

Depois:

```bash
cd <nome-do-servico>
./scripts/local-run.sh
```

## 📁 Estrutura Completa do Projeto (SkyFolder)

```bash
<service-name>/
│
├── pom.xml
├── Dockerfile
├── README.md
├── .gitignore
├── scripts/
│   └── local-run.sh
│
└── src/
    ├── main/
    │   ├── java/
    │   │   └── io/viana/<service_safe>/
    │   │       ├── controller/
    │   │       ├── service/
    │   │       ├── producer/
    │   │       ├── consumer/
    │   │       ├── config/
    │   │       ├── exception/
    │   │       ├── dto/
    │   │       │   └── events/
    │   │       ├── model/
    │   │       ├── repository/
    │   │       └── <MainClass>Application.java
    │   │
    │   └── resources/
    │       ├── application.yml
    │       └── logback.xml (opcional)
    │
    └── test/
        └── java/
            └── io/viana/<service_safe>/
```

Onde:
```console
<service-name> - igual ao nome informado (ex: payment-service)
<service_safe> - hífens substituídos por _ (ex: payment_service)
<MainClass> - PascalCase (ex: PaymentService)
```

## 🧩 Papeis e Responsabilidades das Camadas

1. `controller/`

  - **Exposição HTTP REST**.
  - Responsabilidades:
    - Definir rotas REST
    - Validar requests
    - Chamar o service layer  
    - Retornar respostas consistentes
    - Conversão Request/Response DTO
    - Nunca contém regra de negócio.

2. `service/`

  - **Núcleo de domínio da aplicação**.
  - Responsabilidades:
    - Regras de negócio
    - Operações de domínio
    - Fluxos principais
    - Integrações orquestradas
    - Publicação de eventos
    - Atualização do estado
  - É a camada central do microserviço.

3. `producer/`

  - **Produtores Kafka padrão da plataforma**.
  - Responsabilidades:
    - Enviar eventos para tópicos
    - Padronizar logs de saída
    - Evitar poluição da camada de serviço
  - Cada método representa um evento de domínio.

4. `consumer/`

  - **Consumidores Kafka**.
  - Responsabilidades:
    - Ler eventos externos
    - Delegar para o service
    - Aplicar consistência interna
    - Evitar lógica pesada no listener
  - Listeners devem ser sempre "finos".

5. `config/`

  - **Configurações centralizadas**.
  - Exemplos:
    - Kafka ProducerFactory / ConsumerFactory
    - SerDes
    - WebConfig
    - Actuator / CORS / Security
    - Conexões externas

6. `exception/`

  - **Tratamento global**.
    - Exceptions de domínio
    - Exceptions de validação
    - GlobalExceptionHandler
  - Padrão de respostas:
    - 400 - erros de domínio (ex: estoque insuficiente)
    - 404 - recurso não encontrado
    - 500 - falhas inesperadas

7. `model/`

  - **Entidades de persistência**.
    - JPA Entities
    - Database schema
    - Converter dados de/para o domínio

8. `repository/`

  - **Acesso ao armazenamento**.
    - Interfaces Spring Data
    - Queries customizadas
    - Não contém regra de negócio

9. `dto/`

  - Objetos de transferência:
    - dto/events/
  - Eventos trafegados no Kafka, como:
  ```bash
  ProductCreatedEvent
  OrderCreatedEvent
  PaymentProcessedEvent
  InventoryUpdatedEvent
  ```
Todos seguem padrão `camelCase` e estruturas previsíveis.

## 📦 Dependências Padrão do SkyFolder

O POM padrão gerado possui:

| Dependência                      | Artefato                         | Finalidade / Uso |
|----------------------------------|----------------------------------|-------------------|
| ✔ **Spring Web**                 | `spring-boot-starter-web`        | APIs REST |
| ✔ **Spring Kafka**               | `spring-kafka`                   | Producers e Consumers Kafka |
| ✔ **Spring Data JPA**            | `spring-boot-starter-data-jpa`   | Persistência relacional |
| ✔ **H2 Database (runtime)**      | `com.h2database:h2`              | Banco em memória para desenvolvimento e testes |
| ✔ **Lombok**                     | `lombok`                         | Redução de boilerplate (getters, setters, builders) |
| ✔ **Spring Actuator**            | `spring-boot-starter-actuator`   | Endpoints de observabilidade: `/actuator/health`, `/actuator/info`, `/actuator/metrics` |
| ✔ **Spring Test**                | `spring-boot-starter-test`       | Testes (JUnit + Mockito + utilitários do Spring) |

## 🏗 Padrões Arquiteturais Aplicados

| Padrão / Princípio                          | Descrição |
|----------------------------------------------|-----------|
| ✔ **Event-Driven Architecture**              | Todos os serviços utilizam **Kafka Producer**, **Kafka Consumer** e **eventos de domínio** como mecanismo principal de comunicação. |
| ✔ **Single Bounded Context por Microserviço**| Cada serviço possui sua própria **API**, **lógica de domínio**, **storage** e **producers/consumers**, garantindo isolamento e autonomia. |
| ✔ **Observabilidade Nativa**                 | Actuator habilitado por padrão (health, metrics, info), permitindo monitoramento imediato. |
| ✔ **Padronização rígida de diretórios**      | Estrutura uniforme entre todos os serviços, reduzindo divergências arquiteturais. |
| ✔ **Package-safe (sem hífens)**              | Serviços com nome `order-service` geram pacote seguro como `order_service`. |
| ✔ **Convenção MainClass**                    | Nome do serviço convertido para PascalCase: <br> `order-service` → `OrderServiceApplication`. |


## 📌 Benefícios do SkyFolder

- Acelera criação de novas stacks
- Evita inconsistências
- Todos os serviços já nascem com o mínimo necessário
- Segue padrões modernos de EDA
- Ajuda a manter qualidade arquitetural
- Reduz erros de pacote, nomes e configurações
- Facilita onboarding de novos devs