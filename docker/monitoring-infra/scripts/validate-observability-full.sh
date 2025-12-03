#!/usr/bin/env bash
# validate-observability-full.sh
# Diagnóstico completo (30 checks) para Observability Stack:
# Prometheus, Grafana, Loki, Tempo, Promtail, Alertmanager, Node Exporter, Redis, OTEL Collector
#
# Versão FINAL (usa docker inspect + jq para detecção de volumes - requisito: jq instalado).
# Uso:
#   chmod +x validate-observability-full.sh
#   ./validate-observability-full.sh
set -euo pipefail

# ==========================================================================================
# CONFIGURAÇÃO
# ==========================================================================================
BASE_DIR="$(cd "$(dirname "$0")" && pwd)"
COMPOSE_FILE="./docker-compose-observability.yml"

PROMETHEUS_URL="http://localhost:9090"
GRAFANA_URL="http://localhost:3000"
LOKI_URL="http://localhost:3100"
TEMPO_URL="http://localhost:3200"
ALERTM_URL="http://localhost:9093"
BLACKBOX_URL="http://localhost:9115"

# Ajuste conforme sua senha real do Grafana
GRAFANA_USER="admin"
GRAFANA_PASS="admin"

OTEL_GRPC_PORT=4317
OTEL_HTTP_PORT=4318

VOLUMES=(
  "observability-infra_loki-data"
  "observability-infra_prometheus-data"
  "observability-infra_grafana-data"
  "observability-infra_tempo-data"
  "observability-infra_alertmanager-data"
)

REQUIRED_CONTAINERS=(
  "prometheus"
  "grafana"
  "loki"
  "tempo"
  "promtail"
  "alertmanager"
  "node-exporter"
  "monitor-redis"
)

# ==========================================================================================
# CORES E HELPERS
# ==========================================================================================
GREEN="\033[0;32m"
RED="\033[0;31m"
YELLOW="\033[1;33m"
CYAN="\033[0;36m"
BOLD="\033[1m"
RESET="\033[0m"

ok()    { printf "${GREEN}OK${RESET}"; }
fail()  { printf "${RED}FAIL${RESET}"; }
warn()  { printf "${YELLOW}WARN${RESET}"; }

# ==========================================================================================
# VERIFICA JQ (requisito para o método robusto)
# ==========================================================================================
JQ_BIN="$(command -v jq || true)"
if [ -z "$JQ_BIN" ]; then
  echo "AVISO: 'jq' não encontrado no PATH. O Check #7 usará fallback mais frágil (grep)."
  JQ_BIN=""
fi

# ==========================================================================================
# BARRA DE PROGRESSO (simples)
# ==========================================================================================
progress_bar() {
    local duration=${1:-2}
    local width=28
    local start now elapsed pct fill i
    printf "   ["
    for ((i=0;i<width;i++)); do printf " "; done
    printf "] 0%%"
    start=$(date +%s)
    while true; do
        now=$(date +%s)
        elapsed=$((now - start))
        pct=$(( elapsed * 100 / duration ))
        [ "$pct" -gt 100 ] && pct=100
        fill=$(( pct * width / 100 ))
        printf "\r   ["
        for ((i=0;i<fill;i++)); do printf "="; done
        for ((i=fill;i<width;i++)); do printf " "; done
        printf "] %3d%%" "$pct"
        [ "$pct" -ge 100 ] && break
        sleep 0.12
    done
    echo
}

# ==========================================================================================
# CHECK COUNTER
# ==========================================================================================
check_counter=0
check() {
  check_counter=$((check_counter+1))
  printf "\n%02d) %s\n" "$check_counter" "$1"
}

# ==========================================================================================
# START
# ==========================================================================================
echo -e "${BOLD}${CYAN}"
echo "Observability diagnostics (30 checks)"
echo -e "${RESET}"
echo "Base dir: $BASE_DIR"
echo "Compose:  $COMPOSE_FILE"
echo "Timestamp: $(date -u +"%Y-%m-%dT%H:%M:%SZ")"
echo "----------------------------------------"

# 01 Docker daemon running
check "Docker daemon running"
if docker info >/dev/null 2>&1; then echo -n "   docker: " && ok && echo; else echo -n "   docker: " && fail && echo " (docker daemon not reachable)"; fi

# 02 docker-compose file present
check "docker-compose file present"
if [ -f "$COMPOSE_FILE" ]; then echo -n "   compose file: " && ok && echo " ($COMPOSE_FILE)"; else echo -n "   compose file: " && fail && echo " ($COMPOSE_FILE missing)"; fi

# 03 docker compose version
check "docker compose version"
if docker compose version >/dev/null 2>&1; then echo -n "   compose version: " && docker compose version --short || true; else echo -n "   compose: " && warn && echo " (not installed)"; fi

# 04 Required containers exist (any state)
check "Required containers exist (any state)"
for c in "${REQUIRED_CONTAINERS[@]}"; do
  if docker ps -a --format '{{.Names}}' | grep -q "^${c}$"; then
    echo -n "   container $c: " && ok && echo
  else
    echo -n "   container $c: " && fail && echo " (not found)"
  fi
done

# 05 Required containers are UP
check "Required containers are UP"
progress_bar 2
for c in "${REQUIRED_CONTAINERS[@]}"; do
  if docker ps --format '{{.Names}}' | grep -q "^${c}$"; then
    echo -n "   $c: " && ok && echo
  else
    echo -n "   $c: " && warn && echo " (not running)"
  fi
done

# 06 Docker volumes exist
check "Docker volumes exist"
progress_bar 2
for v in "${VOLUMES[@]}"; do
  if docker volume ls --format '{{.Name}}' | grep -q "^${v}$"; then
    echo -n "   $v: " && ok && echo
  else
    echo -n "   $v: " && fail && echo
  fi
done

# 07 Volume mountpoints accessible + mounted by containers (robusto: docker inspect + jq; fallback: inspect mountpoint + grep)
check "Volume mountpoints accessible + mounted by containers"

for v in "${VOLUMES[@]}"; do
  # inspect volume to get mountpoint
  if ! mp="$(docker volume inspect "$v" -f '{{ .Mountpoint }}' 2>/dev/null || true)"; then
    echo -n "   $v: " && fail && echo " (volume inspect failed)"
    continue
  fi

  # if jq available, use robust container detection
  if [ -n "$JQ_BIN" ]; then
    # list containers and check their Mounts[].Source against volume Mountpoint
    mounted_by=""
    while IFS= read -r cname; do
      # inspect container mounts as JSON
      mounts_json="$(docker inspect --format '{{json .Mounts}}' "$cname" 2>/dev/null || echo "[]")"
      if [ -n "$mounts_json" ] && [ "$mounts_json" != "null" ]; then
        found=$(printf '%s' "$mounts_json" | "$JQ_BIN" --arg mp "$mp" 'map(select(.Source==$mp)) | length > 0' 2>/dev/null || echo "false")
        if [ "$found" = "true" ]; then
          mounted_by="$mounted_by$cname "
        fi
      fi
    done < <(docker ps -a --format '{{.Names}}')

    if [ -z "$mounted_by" ]; then
      echo -n "   $v: " && warn && echo " (not mounted by any container)"
    else
      echo -n "   $v: " && ok && echo " (mounted by container(s): ${mounted_by})"
    fi

  else
    # fallback: check if any container inspect output contains the mountpoint path
    mounted_by=""
    while IFS= read -r cname; do
      if docker inspect "$cname" 2>/dev/null | grep -qF "$mp"; then
        mounted_by="$mounted_by$cname "
      fi
    done < <(docker ps -a --format '{{.Names}}')

    if [ -z "$mounted_by" ]; then
      echo -n "   $v -> $mp: " && warn && echo " (mountpoint missing / not referenced by containers)"
    else
      echo -n "   $v -> $mp: " && ok && echo " (mounted by container(s): ${mounted_by})"
    fi
  fi
done

# 08 Loki config file exists on host
check "Loki config file exists on host"
LOKI_CFG="$BASE_DIR/loki/loki-config.yml"
if [ -f "$LOKI_CFG" ]; then echo -n "   $LOKI_CFG: " && ok && echo; else echo -n "   $LOKI_CFG: " && fail && echo; fi

# 09 Prometheus config file exists on host
check "Prometheus config file exists on host"
PROM_CFG="$BASE_DIR/prometheus/prometheus.yml"
if [ -f "$PROM_CFG" ]; then echo -n "   $PROM_CFG: " && ok && echo; else echo -n "   $PROM_CFG: " && fail && echo; fi

# 10 Grafana reachable (HTTP)
check "Grafana HTTP reachable"
progress_bar 2
if curl -s -m 5 "$GRAFANA_URL" >/dev/null 2>&1; then echo -n "   $GRAFANA_URL: " && ok && echo; else echo -n "   $GRAFANA_URL: " && fail && echo; fi

# 11 Prometheus reachable (HTTP)
check "Prometheus HTTP reachable (metrics check only)"
progress_bar 1
curl -s -m 5 "$PROMETHEUS_URL/metrics" >/dev/null \
  && echo "   $PROMETHEUS_URL/metrics: $(ok)" \
  || echo "   $PROMETHEUS_URL: $(fail)"

# 12 Loki HTTP reachable and /ready
check "Loki HTTP reachable and /ready"
progress_bar 1
if curl -s -m 5 "$LOKI_URL/ready" | grep -qi "ready"; then echo -n "   $LOKI_URL/ready: " && ok && echo; else echo -n "   $LOKI_URL/ready: " && warn && echo; fi

# 13 Tempo HTTP reachable (health)
check "Tempo HTTP reachable"
progress_bar 2
if curl -s -m 5 "$TEMPO_URL/ready" >/dev/null 2>&1; then echo -n "   $TEMPO_URL/ready: " && ok && echo; else echo -n "   $TEMPO_URL/ready: " && warn && echo; fi

# 14 Alertmanager reachable
check "Alertmanager HTTP reachable"
progress_bar 1
if curl -s -m 5 "$ALERTM_URL/-/ready" >/dev/null 2>&1; then echo -n "   $ALERTM_URL/-/ready: " && ok && echo; else echo -n "   $ALERTM_URL: " && warn && echo; fi

# 15 Critical ports listening locally
check "Critical ports listening locally"
PORTS=(9090 3000 3100 3200 4317 4318 9093 9115)
for p in "${PORTS[@]}"; do
  if ss -ltn 2>/dev/null | grep -E ":${p}\b" >/dev/null 2>&1; then
    echo -n "   port $p: " && ok && echo
  else
    echo -n "   port $p: " && warn && echo " (not listening)"
  fi
done

# 16 Prometheus scrape - self target (up)
check "Prometheus scrape - self target (up)"
progress_bar 2
if curl -s -m 7 "$PROMETHEUS_URL/api/v1/query?query=up" | grep -q '"status":"success"'; then
  echo -n "   prometheus query: " && ok && echo
else
  echo -n "   prometheus query: " && fail && echo
fi

# 17 Grafana datasources via API (admin/admin)
check "Grafana datasources"

DATASRC_RAW=$(curl -s -m 5 -u "$GRAFANA_USER:$GRAFANA_PASS" "$GRAFANA_URL/api/datasources" || echo "")

# Verifica se JSON é válido
if echo "$DATASRC_RAW" | jq empty >/dev/null 2>&1; then
    COUNT=$(echo "$DATASRC_RAW" | jq 'length')

    echo -n "   grafana api: " && ok && echo " ($COUNT datasources encontrados)"
else
    echo -n "   grafana api: " && fail && echo " (resposta inválida ou não JSON)"
fi

# 18 Alertmanager alerts API
check "Alertmanager API alerts"
if curl -s -m 5 "$ALERTM_URL/api/v2/alerts" >/dev/null 2>&1; then echo -n "   alertmanager /api/v2/alerts: " && ok && echo; else echo -n "   alertmanager api: " && warn && echo; fi

# 19 Promtail logs
check "Promtail logs"
if docker ps --format '{{.Names}}' | grep -q "^promtail$"; then
  echo -n "   promtail running: " && ok && echo
  echo "   last 5 log lines:"
  docker logs --tail 5 promtail || true
else
  echo -n "   promtail running: " && fail && echo
fi

# 20 Loki volume write test using container shell
check "Loki volume write test using container shell"
progress_bar 3

LOKI_VOLUME=$(docker volume ls --format '{{.Name}}' | grep -E "loki-data$" | head -n 1 || true)

if [ -z "${LOKI_VOLUME:-}" ]; then
  echo -n "   Loki volume: " && fail && echo " (not found)"
else
  echo "   Detected Loki volume: $LOKI_VOLUME"
  # ensure LOKI_CFG exists (already checked earlier but safe)
  if [ ! -f "${LOKI_CFG:-}" ]; then
    echo -n "   write test: " && warn && echo " (loki config missing)"
  else
    if docker run --rm \
          -v "$LOKI_CFG":/etc/loki/config.yml:ro \
          -v "$LOKI_VOLUME":/loki \
          --entrypoint sh \
          grafana/loki:2.9.1 \
          -c "sh -c 'touch /loki/validate_test_$$ || exit 2'"; then
      echo -n "   write test: " && ok && echo
    else
      echo -n "   write test: " && warn && echo " (container cannot write to volume — check permissions)"
    fi
  fi
fi

# 21 Disk free space (/var/lib/docker)
check "Disk free space (/var/lib/docker)"
DF=$(df -h /var/lib/docker 2>/dev/null | tail -1 | awk '{print $4}')
echo "   /var/lib/docker free: ${DF:-unknown}"

# 22 Docker and Compose versions
check "Docker and Compose versions"
echo -n "   docker: " && docker --version || true
echo -n "   compose: " && docker compose version --short || true

# 23 Node Exporter metrics reachable
check "Node Exporter metrics reachable (/metrics)"
if curl -s -m 5 http://localhost:9100/metrics >/dev/null 2>&1; then echo -n "   node exporter: " && ok && echo; else echo -n "   node exporter: " && warn && echo; fi

# 24 Prometheus targets page quick check
check "Prometheus targets page"
if curl -s -m 5 "$PROMETHEUS_URL/api/v1/targets" | grep -q "Targets"; then echo -n "   prometheus targets page: " && ok && echo; else echo -n "   prometheus targets page: " && warn && echo; fi

# 25 Loki /ready and /metrics
check "Loki /ready and /metrics"
progress_bar 1
if curl -s -m 5 "$LOKI_URL/ready" >/dev/null 2>&1; then echo -n "   loki /ready: " && ok && echo; else echo -n "   loki /ready: " && warn && echo; fi
if curl -s -m 5 "$LOKI_URL/metrics" >/dev/null 2>&1; then echo -n "   loki /metrics: " && ok && echo; else echo -n "   loki /metrics: " && warn && echo; fi

# 26 Tempo health
check "Tempo health (/-/healthy)"
if curl -s -m 5 "$TEMPO_URL/-/healthy" >/dev/null 2>&1; then echo -n "   tempo healthy: " && ok && echo; else echo -n "   tempo healthy: " && warn && echo; fi

# 27 Prometheus 'up' series count
check "Prometheus 'up' series count"
UP_COUNT=$(curl -s -m 7 "$PROMETHEUS_URL/api/v1/query?query=count(up)" | "$JQ_BIN" -r '.data.result[0].value[1]' 2>/dev/null || echo "0")
echo "   prometheus up count: ${UP_COUNT:-0}"

# 28 System memory and CPU
check "System memory and CPU"
echo -n "   Mem total/free: "
free -h | awk 'NR==2 {print $2 " / " $4}'
echo -n "   CPU load (1m): "
uptime | sed 's/.*load average: //' | cut -d, -f1

# 29 docker compose ps
check "docker compose ps"
docker compose -f "$COMPOSE_FILE" ps --all || true

# 30 Summary
check "Summary"
echo "   Ran $check_counter checks."
echo "   If WARN/FAIL appeared, review the hints below."
echo
echo "   Common fixes:"
echo "     - Ensure config files are mounted with absolute paths."
echo "     - Create /loki/* directories inside the Loki volume and set ownership: sudo chown -R 10001:10001 /var/lib/docker/volumes/<your_loki_volume>/_data"
echo "     - Validate Tempo config and ports (3200, 4317, 4318)."
echo "     - Check Grafana credentials (default admin/admin attempted)."
echo "     - If using the jq-based check, install jq: (sudo apt install -y jq) or equivalent."

echo
echo -e "${BOLD}Diagnostics finished.${RESET}"
