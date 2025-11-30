# PLATAFORMA ECOMMERCE – GUIA COMPLETO DE EXECUÇÃO (DOCKER + INFRA + APPS)

Este documento descreve como preparar, configurar e executar toda a plataforma de microservices do projeto ecommerce-observability-platform, incluindo:

- Infraestrutura (Kafka, Zookeeper, HAProxy, Kafka UI)
- Microserviços (gateway, product, inventory, cart, order, payment, notification)
- Scripts de build e configuração
- Execução do fluxo completo end-to-end de teste

# 1. PRÉ-REQUISITOS

Antes de iniciar, garanta que seu ambiente atende aos requisitos abaixo.

## 1.1. Docker

Necessário para executar containers da infraestrutura e dos microserviços.

Verifique:
```bash
docker --version
```

## 1.2. Docker Compose

Necessário para orquestrar os containers.

Verifique:
```bash
docker compose version
```

## 1.3. Maven

Necessário para gerar os artefatos JAR de cada microserviço.

Verifique:
```bash
mvn -version
```

## 1.4. Portas necessárias

Certifique-se de que as portas abaixo não estão ocupadas:

| Componente   | Porta |
| ------------ | ----- |
| Gateway      | 8080  |
| Product      | 8081  |
| Inventory    | 8082  |
| Cart         | 8083  |
| Order        | 8084  |
| Payment      | 8085  |
| Notification | 8086  |
| Kafka UI     | 8088  |
| Kafka        | 9092  |
| Zookeeper    | 2181  |
| HAProxy      | 81    |


# 2. PREPARAÇÃO DO AMBIENTE

A raiz do projeto contém scripts para facilitar a inicialização.

## 2.1. Adicionar DNS dos serviços no /etc/hosts

Execute o script para adicionar automaticamente os hosts:
```bash
cd scripts
sudo ./add-ecommerce-hosts.sh
```

Esse script adiciona entradas como:
```console
127.0.0.1 product-service
127.0.0.1 inventory-service
127.0.0.1 cart-service
...
127.0.0.1 kafka
```

Esses aliases permitem comunicação entre host e containers sem depender de IPs.

# 3. INFRAESTRUTURA – DOCKER COMPOSE

A infraestrutura é composta por:

- Zookeeper
- Kafka
- HAProxy (expondo Kafka para o host)
- Kafka UI
- Rede ecommerce-net (compartilhada com microserviços)

## 3.1. Subindo a infraestrutura

```bash
cd infra-stack/ecommerce-infra
docker compose up -d
```

## 3.2. Validando

Kafka via HAProxy
```bash
nc -z localhost 9092
```

Acessar Kafka UI
```bash
http://localhost:8088
```

Se Kafka aparecer conectado, sua infraestrutura está OK.

# 4. GERANDO OS ARTEFATOS DOS MICROSSERVIÇOS

Na raiz do projeto existe o script:

```bash
./build.all.sh
```

Esse script executa:

- mvn clean package -DskipTests
- Para todos os serviços:
  - gateway
  - product
  - inventory
  - cart
  - order
  - payment
  - notification

Ao final, cada serviço terá seu JAR em:
```console
ecommerce-stack/.../target/*.jar
```

# 5. APLICAÇÕES – DOCKER COMPOSE

Após gerar os JARs, execute o docker-compose das aplicações:

```bash
cd docker/ecommerce-apps
docker compose up -d
```

A stack levanta automaticamente:

- gateway
- product-service
- inventory-service
- cart-service
- order-service
- payment-service
- notification-service

Todos conectados à rede ecommerce-net (compartilhada com a infra).

# 6. VALIDAÇÃO DOS SERVIÇOS

Cada microserviço implementa healthcheck via Actuator.

Você pode validar manualmente:
```bash
curl http://localhost:8080/actuator/health     # gateway
curl http://localhost:8081/actuator/health     # product
curl http://localhost:8082/actuator/health     # inventory
curl http://localhost:8083/actuator/health     # cart
curl http://localhost:8084/actuator/health     # order
curl http://localhost:8085/actuator/health     # payment
curl http://localhost:8086/actuator/health     # notification

```

# 7. TESTE END-TO-END COMPLETO

Na pasta `scripts`, existe o script automatizado:

```bash
./test-order-flow.sh
```

Esse script realiza todo fluxo real:

1. Valida se todos os serviços estão UP

2. Cria um produto

3. Reabastece estoque (RESTOCK)

4. Consulta estoque atualizado

5. Adiciona item ao carrinho

6. Consulta carrinho

7. Cria pedido

8. Aguarda processamento (Kafka)

9. Valida status final (pago ou recusado)

# 8. ORDEM CORRETA DE EXECUÇÃO

A sequência recomendada:

1. Preparar hosts (apenas a primeira vez)
```bash
sudo ./add-ecommerce-hosts.sh
```

2. Iniciar infraestrutura:
```bash
docker compose -f docker-compose-infra.yml up --build
```

3. Gerar JARs:
```bash
./build.all.sh
```

4. Iniciar microserviços:
```bash
docker compose up -d   # aplicações
```

5. Validar serviços:
```bash
curl .../actuator/health
```

6. Rodar teste automatizado:
```bash
./test-order-flow.sh
```

