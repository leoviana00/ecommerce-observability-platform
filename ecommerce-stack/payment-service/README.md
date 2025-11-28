# 📦 payment-service

Microserviço gerado automaticamente pelo script **generate-service.sh**.
Este projeto segue o padrão arquitetural SkyFolder utilizado por toda a plataforma.

---

# 1. 🎯 Objetivo do Serviço
Descreva aqui o propósito principal do microserviço.  
Exemplo: gerenciar carrinho, processar pagamentos, enviar notificações, controlar estoque etc.

---

# 2. 🧠 Responsabilidades do Serviço

✔ Responsabilidade 1  
✔ Responsabilidade 2  
✔ Responsabilidade 3  

❌ O que este serviço **não faz** (para evitar confusão):  
- Não realiza...  
- Não orquestra...  
- Não mantém...

---

# 3. 🏗️ Arquitetura Interna (SkyFolder)

Estrutura padronizada do microserviço:

```
src/main/java/io/viana/payment_service/
  ├── controller/
  ├── service/
  ├── repository/
  ├── model/
  ├── dto/
  │    └── events/
  ├── producer/
  ├── consumer/
  ├── config/
  ├── exception/
src/main/resources/
```

---

# 4. 📡 APIs Principais

Documente aqui os endpoints expostos pelo serviço.

### Exemplo:
### POST /resource
Descrição...

### GET /resource/{id}
Descrição...

---

# 5. 🔄 Fluxos do Domínio (Happy Path)

1. Passo 1  
2. Passo 2  
3. Passo 3  

Fluxos alternativos (erros, exceções) também podem ser descritos.

---

# 6. 🔗 Integrações

Liste dependências externas:

- inventory-service (exemplo)
- payment-service
- Kafka topics utilizados
- API Gateway

---

# 7. 🛠️ Dependências do Serviço (padrão)

| Dependência | Artefato | Uso |
|-------------|----------|-----|
| Spring Web | `spring-boot-starter-web` | APIs REST |
| Spring Kafka | `spring-kafka` | Producers/consumers |
| Spring Data JPA | `spring-boot-starter-data-jpa` | Persistência |
| H2 Database | `com.h2database:h2` | Ambiente local |
| Lombok | `lombok` | Boilerplate |
| Actuator | `spring-boot-starter-actuator` | Observabilidade |
| Test | `spring-boot-starter-test` | Testes |

---

# 8. 📦 Eventos (se aplicável)

### Eventos produzidos:
- event-1  
- event-2  

### Eventos consumidos:
- event-3  

Estrutura JSON dos eventos deve ser documentada aqui.

---

# 9. 🧪 Testes

Instruções para rodar testes:

```
mvn test
```

Se o serviço usar Testcontainers, documente aqui.

---

# 10. 🚀 Como rodar o serviço

### Local:
```
./scripts/local-run.sh
```

### Docker:
```
docker build -t payment-service .
docker run -p 8080:8080 payment-service
```

---

# 11. 📚 Logs, Métricas e Observabilidade

Endpoints padrão:

| Actuator | Função |
|----------|--------|
| /actuator/health | Healthcheck |
| /actuator/info | Informações |
| /actuator/metrics | Métricas |

Integrações recomendadas:

- Prometheus  
- Grafana  
- Loki  
- Tempo  

---

# 12. 🧭 Roadmap do Serviço

| Item | Status |
|------|--------|
| Funcionalidade A | 🔄 Em andamento |
| Funcionalidade B | ✔ Concluído |
| Funcionalidade C | ⏳ Planejado |

---

# 13. 📝 Notas adicionais

Espaço livre para documentação específica do serviço.

