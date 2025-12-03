#!/bin/bash

echo "====================================================="
echo "⚙️  COMPILANDO TODA A PLATAFORMA ECOMMERCE (Maven)"
echo "====================================================="

ROOT_DIR=$(pwd)

# Lista de serviços Maven
MODULES=(
  "../../monitoring-stack/monitor-state-manager"
  "../../monitoring-stack/monitor-health-service"
  "../../monitoring-stack/monitor-alert-dispatcher"

)

echo
echo "📦 Compilando módulos Maven..."
echo

for MODULE in "${MODULES[@]}"; do
  echo "---------------------------------------------"
  echo "🔨 Compilando módulo: $MODULE"
  echo "---------------------------------------------"

  cd "$ROOT_DIR/$MODULE" || {
    echo "❌ Erro: não foi possível acessar o diretório $MODULE"
    exit 1
  }

  mvn clean package -DskipTests

  if [ $? -ne 0 ]; then
    echo "❌ Falhou ao compilar $MODULE"
    exit 1
  else
    echo "✔ Sucesso ao compilar $MODULE"
  fi

  echo
done

cd "$ROOT_DIR"

echo "====================================================="
echo "🎉 COMPILAÇÃO FINALIZADA COM SUCESSO!"
echo "JARs gerados em cada /target"
echo "====================================================="
