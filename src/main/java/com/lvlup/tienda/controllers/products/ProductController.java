package com.lvlup.tienda.controllers.products;

import com.lvlup.tienda.models.products.Product;
import com.lvlup.tienda.services.products.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // <--- IMPORTAR
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(originPatterns = "*")
@RestController
@RequestMapping("/api/v1/products")
@Validated
@Tag(name = "Productos", description = "API para gestión de productos")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    @Operation(summary = "Obtener todos los productos", description = "Retorna una lista de todos los productos disponibles")
    //@PreAuthorize("hasAuthority('PRODUCT_READ')") // <--- Requiere permiso de lectura (Admin o Cliente)
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.status(HttpStatus.OK).body(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto por ID", description = "Retorna un producto específico por su ID")
    //@PreAuthorize("hasAuthority('PRODUCT_READ')")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(product -> ResponseEntity.ok(product))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/categoria/{categoria}")
    @Operation(summary = "Buscar productos por categoría", description = "Retorna todos los productos de una categoría específica")
    //@PreAuthorize("hasAuthority('PRODUCT_READ')")
    public ResponseEntity<List<Product>> getProductsByCategoria(@PathVariable String categoria) {
        return ResponseEntity.ok(productService.getProductsByCategoria(categoria));
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar productos por nombre", description = "Busca productos que contengan el texto especificado en su nombre")
    //@PreAuthorize("hasAuthority('PRODUCT_READ')")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String q) {
        return ResponseEntity.ok(productService.searchProducts(q));
    }

    @PostMapping
    @Operation(summary = "Crear producto", description = "Crea un nuevo producto (Requiere permiso PRODUCT_CREATE)")
    @PreAuthorize("hasAuthority('PRODUCT_CREATE')") // <--- Solo quien tenga este permiso (ej: Admin/Vendedor)
    public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.saveProduct(product));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar producto", description = "Actualiza los datos de un producto existente (Requiere autenticación)")
    // TEMPORAL: Comentado para permitir checkout - En producción debería ser un endpoint dedicado /api/v1/orders/checkout
    //@PreAuthorize("hasAuthority('PRODUCT_UPDATE')")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @Valid @RequestBody Product product) {
        return ResponseEntity.ok(productService.updateProduct(id, product));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar producto", description = "Elimina un producto del catálogo (Requiere permiso PRODUCT_DELETE)")
    @PreAuthorize("hasAuthority('PRODUCT_DELETE')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}