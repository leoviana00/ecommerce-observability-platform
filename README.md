# 📦 ecommerce-observability-platform

Plataforma modular de e-commerce orientada a eventos, construída em Java 21 + Spring Boot 3, seguindo padrões de arquitetura distribuída, mensageria com Apache Kafka, observabilidade avançada (metrics, logs, traces) e princípios de resiliência.

O objetivo é demonstrar uma arquitetura completa composta por:
  - `ecommerce-stack`: microserviços funcionais (gateway, produtos, estoque, carrinho, pedidos, pagamentos).
  - `monitoring-stack`: serviços especializados em observabilidade, saúde do ecossistema e despacho automático de alertas.
  - `infra-stack`: infraestrutura, templates de deploy e componentes de suporte.
  - `docs`: documentação, diagramas C4, fluxos e padrões da plataforma.

A plataforma foi projetada para ser extensível, observável e totalmente orientada a eventos, permitindo experimentação com SAGA, idempotência, tolerância a falhas e rastreamento distribuído.

# 🧭 Visão Geral da Documentação

## 📌 Planejamento

Documentos iniciais que fundamentam decisões, requisitos e escopo.

[Ideia e planejamento inicial](./docs/Planejamento.md)

## 🧱 Arquiteturas

🔹 Arquitetura da stack eCommerce

Visão de serviços de domínio e integrações REST + Kafka.

- [ecommerce-architecture.md](./docs/ecommerce-stack/ecommerce-architecture.md)

🔹 Arquitetura da stack de Observabilidade

Fluxo de coleta, correlação, notificação e mecanismos de watchers.

- [observability-architecture.md](./docs/monitoring-stack/observability-architecture.md)

🔹 Arquitetura da stack de Infraestrutura

Componentes infra, deploy local/distribuído e integrações.

- [ecommerce-infra.md](./docs/infra-stack/ecommerce-infra.md)

## 📘 SkyFolder — Padrões do Projeto

Contém diretrizes oficiais de implementação, layout de pastas, convenções e boas práticas para todos os microsserviços:

- [Padrão de Microserviços da plataforma](./docs/Skyfolder.md)

## 🧩 Kafka — Padronização de Eventos

Define padronização de nomenclatura, contratos, versionamento, serialização, validação e estratégias de consumo:

- [Padronização de Producers e Consumers Kafka](./docs/Padronizacao_eventos_kafka.md)

## 🚀 Execução (DOCKER + INFRA + APPS)

- [Rodar a stack](./docs/run-local-environment.md)

## 🌐 Fluxo — Cliente → Pedido

Documenta fim a fim o fluxo do cliente, desde a criação de produto, ações no carrinho, criação do pedido e SAGA de pagamento:

- [Fluxo completo do pedido](./docs/fluxo_pedido.md)

## 📋 Roadmap

Evoluções planejadas da plataforma e etapas futuras:

- [Roadmap inicial](./docs/Roadmap.md)

## 🛠 Débitos Técnicos

Lista organizada de melhorias, otimizações e reestruturações planejadas:

- [Débitos técnicos](./docs/melhorias.md)