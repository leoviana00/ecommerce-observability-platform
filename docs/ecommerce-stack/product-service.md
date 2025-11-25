## 🟦 1. product-service

## 🎯 Objetivo

Gerenciar produtos, disponibilizar dados de catálogo e fornecer endpoints para o gateway e outros serviços.

## 🧩 Funcionalidades

- CRUD básico de produtos
- Endpoints REST via WebFlux
- Repositórios em memória (fase inicial)
- Preparado para DB real (PostgreSQL)

## 🧱 Estrutura

```console
product-service
 ├── controller/
 ├── service/
 ├── repository/
 ├── model/
 └── ProductServiceApplication.java
```

## ⚙ Rotas

```bash
GET /products
GET /products/{id}
POST /products
PUT /products/{id}
DELETE /products/{id}
```