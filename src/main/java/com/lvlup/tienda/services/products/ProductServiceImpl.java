package com.lvlup.tienda.services.products;

import com.lvlup.tienda.models.products.Product;
import com.lvlup.tienda.repositories.products.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getProductsByCategoria(String categoria) {
        return productRepository.findByCategoria(categoria);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> searchProducts(String nombre) {
        return productRepository.findByNombreContainingIgnoreCase(nombre);
    }

    @Override
    @Transactional
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    @Transactional
    public Product updateProduct(Long id, Product product) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + id));

        existingProduct.setNombre(product.getNombre());
        existingProduct.setCategoria(product.getCategoria());
        existingProduct.setDescripcion(product.getDescripcion());
        existingProduct.setImagen(product.getImagen());
        existingProduct.setPrecio(product.getPrecio());
        existingProduct.setStock(product.getStock());

        return productRepository.save(existingProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}
