## 📘 Planejamento Resumido — ecommerce-gateway

## 🎯 Objetivo do Serviço

O ecommerce-gateway é o ponto de entrada da plataforma, responsável por rotear requisições para todos os microserviços internos, controlar observabilidade, filtros globais, logging e servir como base para autenticação futura.

## 🧱 Estrutura do Projeto

```bash
ecommerce-gateway
 ├── src/main/java/io/viana/ecommercegateway/
 │   ├── config/
 │   ├── filter/
 │   ├── routes/
 │   └── EcommerceGatewayApplication.java
 ├── src/main/resources/application.yml
 └── pom.xml

```

## 🧩 Funcionalidades Implementadas

1. Spring Cloud Gateway
  - Roteamento dinâmico usando LoadBalancer:
    - product-service
    - inventory-service
    - cart-service
    - order-service
    - payment-service

  - Suporte para:
    - Reescrita de path quando necessário
    - Timeouts
    - Fallback com HTTP 503
    - Integração com Netty (WebFlux)

___

2. Filtros Globais
    - RequestLoggingFilter
      - Loga entrada e saída da requisição
      - Gera correlation-id
      - Inclui duração da requisição
      - Loga resposta + status HTTP
    - ErrorHandlingFilter
      - Captura exceções e converte para resposta padronizada
      - Log de erro com correlation-id
      - Retorna HTTP 503 quando o destino está offline

___

3. Configuração de Observabilidade
    - Actuator habilitado
    - Health Check exposto
    - Info endpoint
    - Rotas do Gateway aparecem em /actuator/gateway/routes

    - Pronto para integração futura com:
      - Prometheus
      - Grafana
      - Zipkin / OpenTelemetry

4. Ecosistema de Dependências

    - Spring Boot 3.3.x
    - Spring Cloud 2023
    - Spring Cloud Gateway
    - Spring Boot WebFlux
    - Netty server
    - Lombok
    - Micrometer + Observability (já compatível)

## ⚙️ Rotas Configuradas

```bash
GET /products/**     → lb://product-service
GET /inventory/**    → lb://inventory-service
GET /cart/**         → lb://cart-service
GET /orders/**       → lb://order-service
GET /payments/**     → lb://payment-service
```

## 🧪 Funcionalidades Testadas

- Gateway inicializa corretamente
- Atende requisições em localhost:8080
- Retorna 503 quando downstream está offline
- Logs aparecem com correlation-id
- Testado com:
  - /products/123
  - /payments/123
  - /inventory/123
  - /cart/123
  - /orders/123

## 📌 Próximo passo após o gateway

➡ Construir o product-service, primeiro serviço real que será roteado e consumido pelo gateway.