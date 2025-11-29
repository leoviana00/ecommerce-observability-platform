#!/bin/bash

# =========================================================
# Script: generate-service.sh
# Gera automaticamente a estrutura de um novo microserviço
# =========================================================

if [ -z "$1" ]; then
  echo "❌ ERRO: Informe o nome do serviço."
  echo "Exemplo:"
  echo "   ./generate-service.sh order-service"
  exit 1
fi

SERVICE_NAME=$1

# Nome seguro para pacote Java (troca "-" por "_")
PACKAGE_SAFE=$(echo "$SERVICE_NAME" | sed 's/-/_/g')

# Nome da classe principal (converte: payment-service → PaymentService)
MAIN_CLASS=$(echo "$SERVICE_NAME" | sed -r 's/(^|-)([a-z])/\U\2/g')

BASE_DIR="$SERVICE_NAME/src/main/java/io/viana/$PACKAGE_SAFE"

echo "============================================"
echo "🚀 Criando serviço: $SERVICE_NAME"
echo "📦 Pacote Java: io.viana.$PACKAGE_SAFE"
echo "🧩 Classe principal: ${MAIN_CLASS}Application"
echo "============================================"

mkdir -p $SERVICE_NAME

# =============================
# Gera POM.XML com dependências
# =============================
cat <<EOF > $SERVICE_NAME/pom.xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
                             https://maven.apache.org/xsd/maven-4.0.0.xsd">

    <modelVersion>4.0.0</modelVersion>

    <!-- PARENT DO SPRING BOOT (resolve versões automaticamente) -->
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
        <relativePath/>
    </parent>

    <groupId>io.viana</groupId>
    <artifactId>$SERVICE_NAME</artifactId>
    <version>1.0.0</version>
    <name>$SERVICE_NAME</name>
    <description>Auto-generated microservice</description>

    <properties>
        <java.version>21</java.version>
    </properties>

    <dependencies>

        <!-- WEB -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- KAFKA -->
        <dependency>
            <groupId>org.springframework.kafka</groupId>
            <artifactId>spring-kafka</artifactId>
        </dependency>

        <!-- VALIDATION (Jakarta Validation / Hibernate Validator) -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>

        <!-- JPA + H2 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>

        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>

        <!-- LOMBOK -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>

        <!-- ACTUATOR -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>

        <!-- TEST -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

    </dependencies>

    <build>
        <plugins>
            <!-- PLUGIN DO SPRING BOOT -->
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>
EOF

# =============================
# Diretórios do microserviço
# =============================
mkdir -p $BASE_DIR/controller
mkdir -p $BASE_DIR/service
mkdir -p $BASE_DIR/producer
mkdir -p $BASE_DIR/consumer
mkdir -p $BASE_DIR/config
mkdir -p $BASE_DIR/exception
mkdir -p $BASE_DIR/model
mkdir -p $BASE_DIR/repository
mkdir -p $BASE_DIR/dto/events
mkdir -p $SERVICE_NAME/src/main/resources

# =============================
# Classe principal do serviço
# =============================
cat <<EOF > $BASE_DIR/${MAIN_CLASS}Application.java
package io.viana.$PACKAGE_SAFE;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ${MAIN_CLASS}Application {

    public static void main(String[] args) {
        SpringApplication.run(${MAIN_CLASS}Application.class, args);
    }
}
EOF

# =============================
# application.yml
# =============================
cat <<EOF > $SERVICE_NAME/src/main/resources/application.yml
spring:
  application:
    name: $SERVICE_NAME

  datasource:
    url: jdbc:h2:mem:${SERVICE_NAME}db
    driverClassName: org.h2.Driver
    username: sa
    password:

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: ${SERVICE_NAME}-group
      auto-offset-reset: earliest
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer

server:
  port: 0
EOF

# =============================
# Dockerfile
# =============================
cat <<EOF > $SERVICE_NAME/Dockerfile
FROM eclipse-temurin:21-jdk
COPY target/$SERVICE_NAME-1.0.0.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
EOF

# =============================
# README.md (Template SkyFolder)
# =============================
cat <<EOF > $SERVICE_NAME/README.md
# 📦 $SERVICE_NAME

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

\`\`\`
src/main/java/io/viana/$PACKAGE_SAFE/
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
\`\`\`

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
| Spring Web | \`spring-boot-starter-web\` | APIs REST |
| Spring Kafka | \`spring-kafka\` | Producers/consumers |
| Spring Data JPA | \`spring-boot-starter-data-jpa\` | Persistência |
| H2 Database | \`com.h2database:h2\` | Ambiente local |
| Lombok | \`lombok\` | Boilerplate |
| Actuator | \`spring-boot-starter-actuator\` | Observabilidade |
| Test | \`spring-boot-starter-test\` | Testes |

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

\`\`\`
mvn test
\`\`\`

Se o serviço usar Testcontainers, documente aqui.

---

# 10. 🚀 Como rodar o serviço

### Local:
\`\`\`
./scripts/local-run.sh
\`\`\`

### Docker:
\`\`\`
docker build -t $SERVICE_NAME .
docker run -p 8080:8080 $SERVICE_NAME
\`\`\`

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

EOF

# =============================
# .gitignore
# =============================
cat <<EOF > $SERVICE_NAME/.gitignore
HELP.md
target/
.mvn/wrapper/maven-wrapper.jar
!**/src/main/**/target/
!**/src/test/**/target/

### STS ###
.apt_generated
.classpath
.factorypath
.project
.settings
.springBeans
.sts4-cache

### IntelliJ IDEA ###
.idea
*.iws
*.iml
*.ipr

### NetBeans ###
/nbproject/private/
/nbbuild/
/dist/
/nbdist/
/.nb-gradle/
build/
!**/src/main/**/build/
!**/src/test/**/build/

### VS Code ###
.vscode/

EOF

# =============================
# Script: local-run.sh
# =============================
mkdir -p $SERVICE_NAME/scripts
cat <<EOF > $SERVICE_NAME/scripts/local-run.sh
#!/bin/bash
mvn clean package -DskipTests
java -jar target/$SERVICE_NAME-1.0.0.jar
EOF

chmod +x $SERVICE_NAME/scripts/local-run.sh

echo ""
echo "============================================"
echo "🎉 Serviço criado com sucesso!"
echo "📁 Caminho: $SERVICE_NAME/"
echo "🚀 Execute com:"
echo "   cd $SERVICE_NAME"
echo "   ./scripts/local-run.sh"
echo "============================================"
