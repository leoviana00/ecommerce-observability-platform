package io.viana.ecommerce_gateway.filter;

import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

// Marca a classe como um componente do Spring e um Filtro Global do Gateway
@Component
public class CorrelationIdFilter implements GlobalFilter, Ordered {

    // Define o nome do cabeçalho HTTP usado para transportar o ID de Correlação
    private static final String HEADER = "X-Correlation-ID";

    /**
     * Lógica principal do filtro, executada para cada requisição.
     * @param exchange O contexto da requisição/resposta.
     * @param chain A cadeia de filtros.
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {
        // 1. OBTENÇÃO/CRIAÇÃO DO ID
        
        // Tenta obter o ID de Correlação do cabeçalho da requisição
        String correlationId = exchange.getRequest().getHeaders().getFirst(HEADER);
        
        // Se o cabeçalho não existir ou estiver vazio, gera um novo ID único
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }

        // 2. CONFIGURAÇÃO DO CONTEXTO DE LOG (MDC)
        
        // Coloca o ID no MDC (Mapped Diagnostic Context) do SLF4J.
        // Isso garante que todos os logs gerados durante esta requisição (nesta thread)
        // incluirão o correlationId, facilitando o rastreamento.
        MDC.put("correlationId", correlationId);

        // 3. PROPAGAÇÃO DO ID (Requisição e Resposta)
        
        // Cria uma nova ServerWebExchange mutada
        ServerWebExchange mutatedExchange = exchange.mutate()
                // Adiciona o cabeçalho X-Correlation-ID à requisição ANTES de enviá-la ao microserviço
                .request(exchange.getRequest().mutate().header(HEADER, correlationId).build())
                .response(exchange.getResponse())
                .build();

        // Adiciona o cabeçalho X-Correlation-ID à resposta, para que o cliente também o receba
        mutatedExchange.getResponse().getHeaders().add(HEADER, correlationId);

        // 4. LOG DE ENTRADA
        
        // Log simples mostrando a requisição de entrada no console
        System.out.println("➡️  [" + correlationId + "] " + exchange.getRequest().getMethod() + " " + exchange.getRequest().getURI().getPath());

        // 5. CONTINUAÇÃO DA CADEIA E LIMPEZA
        
        // Continua a cadeia de filtros (processa a requisição)
        return chain.filter(mutatedExchange)
                // Hook para ser executado quando a requisição/resposta for finalizada (sucesso ou falha)
                .doFinally(signal -> MDC.clear()); // LIMPA o MDC para evitar vazamento de dados entre threads reusadas
    }

    /**
     * Define a ordem de execução do filtro.
     * Um valor baixo (-100) garante que este filtro (geralmente o de ID) seja executado
     * bem no início da cadeia de filtros do Gateway.
     */
    @Override
    public int getOrder() {
        return -100;
    }
}