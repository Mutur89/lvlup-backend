package com.lvlup.tienda.models.orders;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="order_items")
@Getter
@Setter
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    @NotNull(message = "El producto es obligatorio")
    private Long productId;

    @Column(name = "product_name", nullable = false)
    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(max=255)
    private String productName;

    @Column(nullable = false)
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer quantity;

    @Column(name = "unit_price", nullable = false)
    @NotNull(message = "El precio unitario es obligatorio")
    @Min(value = 0, message = "El precio debe ser mayor o igual a 0")
    private Integer unitPrice;

    @Column(name = "order_id", nullable = false, insertable = false, updatable = false)
    private Long orderId;

    // Relación Many-to-One con Order
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
}
