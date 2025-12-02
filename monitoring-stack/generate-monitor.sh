#!/bin/bash

# =========================================================
# Script: generate-monitor.sh
# Gera automaticamente a estrutura de um serviço
# da monitoring-stack (Observability Mesh)
#
# Agora inclui:
#   - deployment.yaml
#   - service.yaml
#   - configmap.yaml
#   - secret.yaml
#   - hpa.yaml
#   - kustomization.yaml
# =========================================================

if [ -z "$1" ] || [ -z "$2" ]; then
  echo "❌ ERRO: Informe o nome do serviço e o tipo."
  echo "Exemplo:"
  echo "   ./generate-monitor.sh monitor-health-service health"
  exit 1
fi

SERVICE_NAME=$1
MONITOR_TYPE=$2

PACKAGE_SAFE=$(echo "$SERVICE_NAME" | sed 's/-/_/g')
NAME_SAFE=$(echo "$SERVICE_NAME" | tr '[:upper:]' '[:lower:]')
MAIN_CLASS=$(echo "$SERVICE_NAME" | sed -r 's/(^|-)([a-z])/\U\2/g')

BASE_DIR="$SERVICE_NAME/src/main/java/io/viana/$PACKAGE_SAFE"

echo "============================================"
echo "🚀 Criando monitor: $SERVICE_NAME"
echo "🧩 Tipo: $MONITOR_TYPE"
echo "📦 Pacote: io.viana.$PACKAGE_SAFE"
echo "🧠 Classe principal: ${MAIN_CLASS}Application"
echo "============================================"

mkdir -p $SERVICE_NAME

# =========================================================
# Dependências específicas
# =========================================================

DEP_WEB="
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
"

DEP_ACTUATOR="
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
"

DEP_SCHEDULING="
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>
"

DEP_KAFKA_ADMIN="
        <dependency>
            <groupId>org.springframework.kafka</groupId>
            <artifactId>spring-kafka</artifactId>
        </dependency>
"

DEP_REDIS="
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>
"

DEP_MAIL="
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-mail</artifactId>
        </dependency>
"

DEPS="$DEP_WEB$DEP_ACTUATOR$DEP_SCHEDULING"

case $MONITOR_TYPE in
  health)
    DEPS="$DEPS"
    ;;
  lag)
    DEPS="$DEPS$DEP_KAFKA_ADMIN"
    ;;
  consumer)
    DEPS="$DEPS$DEP_KAFKA_ADMIN"
    ;;
  state)
    DEPS="$DEPS$DEP_REDIS"
    ;;
  alert)
    DEPS="$DEPS$DEP_MAIL"
    ;;
  *)
    echo "❌ Tipo inválido. Use: health | lag | consumer | state | alert"
    exit 1
    ;;
esac

# =========================================================
# POM.XML
# =========================================================
cat <<EOF > $SERVICE_NAME/pom.xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
                             https://maven.apache.org/xsd/maven-4.0.0.xsd">

    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
        <relativePath/>
    </parent>

    <groupId>io.viana</groupId>
    <artifactId>$SERVICE_NAME</artifactId>
    <version>1.0.0</version>

    <properties>
        <java.version>21</java.version>
    </properties>

    <dependencies>

$DEPS

        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>
EOF

# =========================================================
# Diretórios
# =========================================================
mkdir -p $BASE_DIR/controller
mkdir -p $BASE_DIR/service
mkdir -p $BASE_DIR/config
mkdir -p $BASE_DIR/dto
mkdir -p $BASE_DIR/exception
mkdir -p $SERVICE_NAME/src/main/resources
mkdir -p $SERVICE_NAME/scripts
mkdir -p $SERVICE_NAME/k8s

# =========================================================
# Classe principal
# =========================================================
cat <<EOF > $BASE_DIR/${MAIN_CLASS}Application.java
package io.viana.$PACKAGE_SAFE;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ${MAIN_CLASS}Application {

    public static void main(String[] args) {
        SpringApplication.run(${MAIN_CLASS}Application.class, args);
    }
}
EOF

# =========================================================
# application.yml
# =========================================================

APP_YML="spring:
  application:
    name: $SERVICE_NAME
"

case $MONITOR_TYPE in
  lag|consumer)
    APP_YML="$APP_YML

  kafka:
    bootstrap-servers: kafka:9092
"
    ;;
  state)
    APP_YML="$APP_YML

  data:
    redis:
      host: redis
      port: 6379
"
    ;;
esac

echo "$APP_YML" > $SERVICE_NAME/src/main/resources/application.yml

# =========================================================
# Dockerfile
# =========================================================
cat <<EOF > $SERVICE_NAME/Dockerfile
FROM eclipse-temurin:21-jdk
COPY target/$SERVICE_NAME-1.0.0.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
EOF

# =========================================================
# Kubernetes: deployment
# =========================================================
cat <<EOF > $SERVICE_NAME/k8s/deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: $NAME_SAFE
  labels:
    app: $NAME_SAFE
spec:
  replicas: 1
  selector:
    matchLabels:
      app: $NAME_SAFE
  template:
    metadata:
      labels:
        app: $NAME_SAFE
    spec:
      containers:
        - name: $NAME_SAFE
          image: $NAME_SAFE:1.0.0
          envFrom:
            - configMapRef:
                name: $NAME_SAFE-config
            - secretRef:
                name: $NAME_SAFE-secret
          ports:
            - containerPort: 8080
          readinessProbe:
            httpGet:
              path: /actuator/health
              port: 8080
            initialDelaySeconds: 5
            periodSeconds: 5
          livenessProbe:
            httpGet:
              path: /actuator/health
              port: 8080
            initialDelaySeconds: 15
            periodSeconds: 10
EOF

# =========================================================
# Kubernetes: service
# =========================================================
cat <<EOF > $SERVICE_NAME/k8s/service.yaml
apiVersion: v1
kind: Service
metadata:
  name: $NAME_SAFE
spec:
  selector:
    app: $NAME_SAFE
  ports:
    - port: 80
      targetPort: 8080
      protocol: TCP
EOF

# =========================================================
# Kubernetes: configmap
# =========================================================
cat <<EOF > $SERVICE_NAME/k8s/configmap.yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: $NAME_SAFE-config
data:
  LOG_LEVEL: "INFO"
  MONITOR_INTERVAL_SECONDS: "15"
EOF

# =========================================================
# Kubernetes: secret
# =========================================================
cat <<EOF > $SERVICE_NAME/k8s/secret.yaml
apiVersion: v1
kind: Secret
metadata:
  name: $NAME_SAFE-secret
type: Opaque
data:
  GENERIC_API_KEY: ""
EOF

# =========================================================
# Kubernetes: HPA (Horizontal Pod Autoscaler)
# =========================================================
cat <<EOF > $SERVICE_NAME/k8s/hpa.yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: $NAME_SAFE-hpa
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: $NAME_SAFE
  minReplicas: 1
  maxReplicas: 5
  metrics:
    - type: Resource
      resource:
        name: cpu
        target:
          type: Utilization
          averageUtilization: 70
EOF

# =========================================================
# Kubernetes: kustomization.yaml
# =========================================================
cat <<EOF > $SERVICE_NAME/k8s/kustomization.yaml
resources:
  - deployment.yaml
  - service.yaml
  - configmap.yaml
  - secret.yaml
  - hpa.yaml
EOF

# =========================================================
# Script local-run.sh
# =========================================================
cat <<EOF > $SERVICE_NAME/scripts/local-run.sh
#!/bin/bash
mvn clean package -DskipTests
java -jar target/$SERVICE_NAME-1.0.0.jar
EOF

chmod +x $SERVICE_NAME/scripts/local-run.sh

# =========================================================
# README.md
# =========================================================
cat <<EOF > $SERVICE_NAME/README.md
# $SERVICE_NAME

Serviço da monitoring-stack gerado automaticamente pelo script **generate-monitor.sh**.

Tipo: **$MONITOR_TYPE**

---

## Kubernetes

Arquivos gerados em:

\`\`\`
k8s/
  deployment.yaml
  service.yaml
  configmap.yaml
  secret.yaml
  hpa.yaml
  kustomization.yaml
\`\`\`

Aplicar no cluster:

\`\`\`
kubectl apply -k k8s/
\`\`\`

---

## Execução local

\`\`\`
./scripts/local-run.sh
\`\`\`

EOF

echo ""
echo "============================================"
echo "🎉 Monitor criado com sucesso!"
echo "📁 Caminho: $SERVICE_NAME/"
echo "📦 Manifestos K8s: $SERVICE_NAME/k8s/"
echo "🚀 Execute com:"
echo "   cd $SERVICE_NAME"
echo "   ./scripts/local-run.sh"
echo "============================================"
