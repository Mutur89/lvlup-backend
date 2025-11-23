package com.lvlup.tienda.models.carts;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name="cart_items")
@Getter
@Setter
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    @NotNull(message = "El producto es obligatorio")
    private Long productId;

    @Column(nullable = false)
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer quantity;

    @Column(name = "unit_price", nullable = false)
    @NotNull(message = "El precio unitario es obligatorio")
    private BigDecimal unitPrice;

    @Column(name = "cart_id", nullable = false, insertable = false, updatable = false)
    private Long cartId;

    // Relación Many-to-One con Cart
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;
}
