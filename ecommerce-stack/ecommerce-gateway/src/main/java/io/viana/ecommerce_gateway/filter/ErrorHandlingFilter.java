package io.viana.ecommerce_gateway.filter;

import lombok.extern.slf4j.Slf4j; // Anotação para logger
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j // Adiciona um logger (slf4j)
@Component
public class ErrorHandlingFilter implements GlobalFilter, Ordered {

    /**
     * Aplica a lógica de tratamento de erro no fluxo reativo.
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {

        return chain.filter(exchange) // Executa o restante da cadeia de filtros
                .onErrorResume(ex -> { // Em caso de erro em qualquer ponto do fluxo
                    
                    // Registra o erro detalhado (caminho da requisição e mensagem de erro)
                    log.error("❌ Erro durante o processamento da requisição para {}: {}",
                              exchange.getRequest().getURI().getPath(),
                              ex.getMessage());

                    // Define o status da resposta como 503 (Serviço Indisponível)
                    exchange.getResponse().setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
                    
                    // Completa e envia a resposta com o status 503
                    return exchange.getResponse().setComplete();
                });
    }

    /**
     * Define a ordem de execução do filtro. -90 garante que ele seja executado 
     * no início, mas após o CorrelationIdFilter (que tinha ordem -100) para poder capturar seus logs.
     */
    @Override
    public int getOrder() {
        return -90;
    }
}