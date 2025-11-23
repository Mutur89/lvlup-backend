package com.lvlup.tienda.repositories.carts;

import com.lvlup.tienda.models.carts.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}
