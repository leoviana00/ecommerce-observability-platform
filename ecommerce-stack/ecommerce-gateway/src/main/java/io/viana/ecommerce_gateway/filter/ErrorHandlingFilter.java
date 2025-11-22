package io.viana.ecommerce_gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Filtro global para capturar e tratar erros que ocorrem
 * durante o roteamento ou processamento da requisição.
 * * O uso de @Slf4j garante que o Logback capte o correlationId 
 * do MDC/Contexto Reativo automaticamente.
 */
@Slf4j
@Component
public class ErrorHandlingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {

        return chain.filter(exchange)
                .onErrorResume(ex -> {
                    // O Correlation ID é capturado automaticamente pelo Logback (via MDC/Contexto Reativo)
                    // porque estamos usando log.error.
                    
                    log.error("❌ Erro durante o processamento da requisição para {}: {}", 
                              exchange.getRequest().getURI().getPath(), 
                              ex.getMessage());
                              
                    // Define o status HTTP de serviço indisponível
                    exchange.getResponse().setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
                    
                    // Completa a resposta
                    return exchange.getResponse().setComplete();
                });
    }

    @Override
    public int getOrder() {
        // Executa depois do CorrelationIdFilter (HIGHEST_PRECEDENCE) e RequestLoggingFilter
        return -90; 
    }
}