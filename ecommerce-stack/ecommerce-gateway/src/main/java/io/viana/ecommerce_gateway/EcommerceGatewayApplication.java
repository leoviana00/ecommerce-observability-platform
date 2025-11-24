package io.viana.ecommerce_gateway;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


// Marca a classe como uma aplicação Spring Boot. 
// Esta anotação é a combinação de @Configuration, @EnableAutoConfiguration e @ComponentScan.
@SpringBootApplication
public class EcommerceGatewayApplication {

    /**
     * O método principal (main) que inicia a aplicação Spring Boot.
     * @param args Argumentos de linha de comando.
     */
    public static void main(String[] args) {
        // Método estático que configura e inicia a aplicação.
        SpringApplication.run(EcommerceGatewayApplication.class, args);
    }
}