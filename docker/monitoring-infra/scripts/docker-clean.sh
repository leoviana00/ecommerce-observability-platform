#!/usr/bin/env bash
# docker-clean-pro.sh
# Versão avançada com logs coloridos e barra de progresso

set -euo pipefail

# ================================
# CORES ANSI
# ================================
RED="\033[31m"
GREEN="\033[32m"
YELLOW="\033[33m"
CYAN="\033[36m"
RESET="\033[0m"
BOLD="\033[1m"

# ================================
# FLAGS
# ================================
FORCE=false
if [[ "${1:-}" == "--force" ]]; then
  FORCE=true
fi

# ================================
# FUNÇÕES DE UI
# ================================
progress_bar() {
  local msg=$1
  echo -ne "${CYAN}${msg}${RESET} "
  for i in {1..10}; do
    echo -n "▓"
    sleep 0.07
  done
  echo " ${GREEN}[OK]${RESET}"
}

log_info() {
  echo -e "${CYAN}${1}${RESET}"
}

log_warn() {
  echo -e "${YELLOW}${1}${RESET}"
}

log_success() {
  echo -e "${GREEN}${1}${RESET}"
}

log_error() {
  echo -e "${RED}${1}${RESET}"
}

confirm() {
  if [ "$FORCE" = true ]; then
    return 0
  fi
  read -r -p "$(echo -e "${YELLOW}$1 [y/N]: ${RESET}")" resp
  [[ "$resp" =~ ^[Yy]$ ]]
}

# ================================
# HEADER
# ================================
echo -e "${BOLD}${CYAN}"
echo "=============================================="
echo "      DOCKER CLEAN PRO – FULL CLEANUP"
echo "=============================================="
echo -e "${RESET}"

sleep 0.3

# ================================
# 1) STOP CONTAINERS
# ================================
if docker ps -q | grep -q .; then
  if confirm "Parar todos os containers?"; then
    log_info "Parando containers..."
    docker stop $(docker ps -q) > /dev/null 2>&1 || true
    progress_bar "Containers parados"
  else
    log_warn "Parada de containers ignorada."
  fi
else
  log_warn "Nenhum container em execução."
fi

# ================================
# 2) REMOVE CONTAINERS
# ================================
if docker ps -aq | grep -q .; then
  if confirm "Remover todos os containers?"; then
    log_info "Removendo containers..."
    docker rm -f $(docker ps -aq) > /dev/null 2>&1 || true
    progress_bar "Containers removidos"
  else
    log_warn "Remoção de containers ignorada."
  fi
else
  log_warn "Nenhum container existente."
fi

# ================================
# 3) REMOVE IMAGES
# ================================
if docker images -q | grep -q .; then
  if confirm "Remover todas as imagens?"; then
    log_info "Removendo imagens..."
    docker rmi -f $(docker images -q) > /dev/null 2>&1 || true
    progress_bar "Imagens removidas"
  else
    log_warn "Remoção de imagens ignorada."
  fi
else
  log_warn "Nenhuma imagem encontrada."
fi

# ================================
# 4) REMOVE VOLUMES
# ================================
if docker volume ls -q | grep -q .; then
  if confirm "Remover todos os volumes Docker?"; then
    log_info "Removendo volumes..."
    docker volume rm $(docker volume ls -q) > /dev/null 2>&1 || true
    progress_bar "Volumes removidos"
  else
    log_warn "Remoção de volumes ignorada."
  fi
else
  log_warn "Nenhum volume encontrado."
fi

# ================================
# 5) REMOVE NETWORKS
# ================================
NETS=$(docker network ls --format '{{.Name}}' | grep -v -E '^(bridge|host|none)$' || true)
if [[ -n "$NETS" ]]; then
  echo -e "${CYAN}Networks customizadas detectadas:${RESET}"
  echo "$NETS"
  echo

  if confirm "Remover essas networks?"; then
    log_info "Removendo networks..."
    docker network rm $NETS > /dev/null 2>&1 || true
    progress_bar "Networks removidas"
  else
    log_warn "Remoção de networks ignorada."
  fi
else
  log_warn "Nenhuma network customizada encontrada."
fi

# ================================
# 6) CLEAN BUILDER CACHE
# ================================
if confirm "Deseja limpar o Build Cache (docker builder prune)?"; then
  log_info "Limpando cache de build..."
  docker builder prune -f > /dev/null 2>&1 || true
  progress_bar "Build cache limpo"
else
  log_warn "Limpeza de cache ignorada."
fi

# ================================
# FINAL
# ================================
echo ""
echo -e "${GREEN}${BOLD}LIMPEZA COMPLETA!${RESET}"
echo "Use: docker system df  para ver o estado do disco."

echo -e "${CYAN}Script finalizado.${RESET}"
