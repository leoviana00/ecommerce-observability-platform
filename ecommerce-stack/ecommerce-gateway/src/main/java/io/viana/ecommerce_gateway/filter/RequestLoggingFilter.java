package io.viana.ecommerce_gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

// Adiciona um logger (Slf4j)
@Slf4j 
// Marca a classe como um componente do Spring e um Filtro Global do Gateway
@Component
public class RequestLoggingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(org.springframework.web.server.ServerWebExchange exchange,
                             org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {

        // --- 1. REGISTRO DE ENTRADA (BEFORE) ---
        
        // Marca o tempo de início da requisição para calcular a latência
        long start = System.currentTimeMillis();
        String method = exchange.getRequest().getMethod().name();
        String path = exchange.getRequest().getURI().getPath();

        // Loga a entrada da requisição
        log.info("➡️ {} {}", method, path);

        // --- 2. REGISTRO DE SAÍDA (AFTER) ---
        
        // Continua o processamento da cadeia de filtros
        return chain.filter(exchange)
            // Usa 'then' para agendar uma tarefa a ser executada DEPOIS que a requisição for processada
            .then(Mono.fromRunnable(() -> {
                // Calcula o tempo total gasto
                long time = System.currentTimeMillis() - start;
                // Obtém o status HTTP da resposta (usa 0 se o status for nulo)
                int status = exchange.getResponse().getStatusCode() != null ? exchange.getResponse().getStatusCode().value() : 0;
                
                // Loga a saída da requisição, incluindo o status e o tempo de latência
                log.info("⬅️ {} {} (status={}, {}ms)", method, path, status, time);
            }));
    }

    /**
     * Define a ordem de execução do filtro.
     * O valor -1 garante que ele seja executado bem cedo (após o CorrelationIdFilter e ErrorHandlingFilter, 
     * que tinham ordens -100 e -90, respectivamente), mas antes da maioria dos filtros de roteamento.
     */
    @Override
    public int getOrder() {
        return -1;
    }
}