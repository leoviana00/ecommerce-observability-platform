# monitor-lag-service

Serviço da monitoring-stack gerado automaticamente pelo script **generate-monitor.sh**.

Tipo: **lag**

---

## Kubernetes

Arquivos gerados em:

```
k8s/
  deployment.yaml
  service.yaml
  configmap.yaml
  secret.yaml
  hpa.yaml
  kustomization.yaml
```

Aplicar no cluster:

```
kubectl apply -k k8s/
```

---

## Execução local

```
./scripts/local-run.sh
```

