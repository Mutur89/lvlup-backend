package com.lvlup.tienda.repositories.products;

import com.lvlup.tienda.models.products.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategoria(String categoria);

    List<Product> findByNombreContainingIgnoreCase(String nombre);
}
