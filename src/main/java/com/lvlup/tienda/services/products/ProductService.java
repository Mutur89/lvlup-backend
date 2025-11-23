package com.lvlup.tienda.services.products;

import com.lvlup.tienda.models.products.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    List<Product> getAllProducts();

    Optional<Product> getProductById(Long id);

    List<Product> getProductsByCategoria(String categoria);

    List<Product> searchProducts(String nombre);

    Product saveProduct(Product product);

    Product updateProduct(Long id, Product product);

    void deleteProduct(Long id);
}
