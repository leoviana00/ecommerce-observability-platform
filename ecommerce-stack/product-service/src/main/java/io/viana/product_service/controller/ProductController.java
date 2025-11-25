package io.viana.product_service.controller;

import io.viana.product_service.dto.ProductDTO;
import io.viana.product_service.model.ProductEntity;
import io.viana.product_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Define esta classe como um Controller REST.
 * Combina @Controller e @ResponseBody, indicando que todos os métodos
 * retornarão dados (como JSON) diretamente como corpo da resposta HTTP.
 */
@RestController
/**
 * Define o caminho base para todos os endpoints deste controller.
 * Ex: Todas as rotas começarão com /products.
 */
@RequestMapping("/products")
/**
 * Usa o Lombok para gerar um construtor com todos os campos "final" (como o ProductService).
 * Isso facilita a injeção de dependência (DI) via construtor.
 */
@RequiredArgsConstructor
public class ProductController {

    // Injeção do serviço, onde a lógica de negócio real reside.
    private final ProductService service;

    // Criar produto
    /**
     * Mapeia requisições HTTP POST para a URI base: POST /products
     * É usado para criar um novo recurso (produto).
     * @param dto O corpo da requisição é mapeado para o objeto ProductDTO (@RequestBody).
     * @return 200 OK com o ProductEntity criado no corpo.
     */
    @PostMapping
    public ResponseEntity<ProductEntity> createProduct(@RequestBody ProductDTO dto) {
        ProductEntity created = service.createProduct(dto);
        // O serviço aqui deve salvar no DB E publicar um evento Kafka de "Produto Criado"
        return ResponseEntity.ok(created);
    }

    // Listar todos
    /**
     * Mapeia requisições HTTP GET para a URI base: GET /products
     * @return 200 OK com uma lista de todos os produtos.
     */
    @GetMapping
    public ResponseEntity<List<ProductEntity>> listAll() {
        List<ProductEntity> products = service.findAll();
        return ResponseEntity.ok(products);
    }

    // Buscar por ID
    /**
     * Mapeia requisições HTTP GET para a URI com um caminho variável: GET /products/{id}
     * @param id O valor do ID na URI é extraído para a variável 'id' (@PathVariable).
     * @return 200 OK se encontrado, ou 404 Not Found se não existir.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductEntity> getById(@PathVariable Long id) {
        return service.findById(id)
                // Se o Optional contiver um produto, retorna 200 OK.
                .map(ResponseEntity::ok)
                // Caso contrário (vazio), retorna 404 Not Found.
                .orElse(ResponseEntity.notFound().build());
    }

    // Buscar por nome (query param)
    /**
     * Mapeia requisições HTTP GET com um parâmetro de consulta: GET /products/search?name=valor
     * @param name O valor do parâmetro 'name' na URL é extraído (@RequestParam).
     * @return 200 OK com uma lista de produtos que correspondem ao nome.
     */
    @GetMapping("/search")
    public ResponseEntity<List<ProductEntity>> searchByName(@RequestParam String name) {
        List<ProductEntity> products = service.findByName(name);
        return ResponseEntity.ok(products);
    }

    // Produtos disponíveis (estoque > 0)
    /**
     * Mapeia requisições HTTP GET para: GET /products/available
     * Este é um endpoint customizado que representa uma consulta de negócio específica.
     * @return 200 OK com a lista de produtos considerados disponíveis.
     */
    @GetMapping("/available")
    public ResponseEntity<List<ProductEntity>> availableProducts() {
        // Nota: Em um sistema de microserviços real, esta consulta provavelmente
        // envolveria uma chamada para o 'inventory-service' para verificar o estoque.
        List<ProductEntity> products = service.findAvailableProducts();
        return ResponseEntity.ok(products);
    }
}