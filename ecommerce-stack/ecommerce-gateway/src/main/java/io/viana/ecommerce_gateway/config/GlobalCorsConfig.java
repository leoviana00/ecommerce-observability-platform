package io.viana.ecommerce_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

// Marca a classe como uma fonte de definição de beans
@Configuration
public class GlobalCorsConfig {

    // Define um bean para o filtro CORS
    @Bean
    public CorsWebFilter corsWebFilter() {
        // Cria uma nova configuração CORS
        CorsConfiguration config = new CorsConfiguration();
        
        // Permite o envio de credenciais (cookies, headers de autenticação, etc.)
        config.setAllowCredentials(true);
        
        // Define quais origens (domínios) podem acessar a API.
        // List.of("*") permite *qualquer* domínio (CUIDADO: Ajustar isso em produção!)
        config.setAllowedOrigins(List.of("*")); 
        
        // Permite todos os cabeçalhos HTTP na requisição (ex: Authorization, Content-Type)
        config.setAllowedHeaders(List.of("*"));
        
        // Permite os métodos HTTP especificados (GET, POST, PUT, DELETE, OPTIONS)
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Cria a fonte de configuração baseada em URLs
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        
        // Registra a configuração CORS criada acima para TODOS os caminhos ("/**")
        source.registerCorsConfiguration("/**", config);

        // Retorna o filtro CORS usando a fonte de configuração
        return new CorsWebFilter(source);
    }
}