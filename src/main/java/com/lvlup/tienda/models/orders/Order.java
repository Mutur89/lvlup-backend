package com.lvlup.tienda.models.orders;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.lvlup.tienda.models.audit.Audit;
import com.lvlup.tienda.models.users.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="orders")
@Getter
@Setter
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    @NotNull(message = "El usuario es obligatorio")
    private Long userId;

    @Column(nullable = false)
    @NotNull(message = "El total es obligatorio")
    @Min(value = 0, message = "El total debe ser mayor o igual a 0")
    private Integer total;

    @Column(nullable = false)
    @NotNull(message = "El descuento es obligatorio")
    @Min(value = 0, message = "El descuento debe ser mayor o igual a 0")
    private Integer descuento;

    @Column(nullable = false)
    @NotBlank(message = "El estado es obligatorio")
    @Size(max=255)
    private String estado;

    @Column(name = "metodo_pago", nullable = false)
    @NotBlank(message = "El método de pago es obligatorio")
    @Size(max=255)
    private String metodoPago;

    @Column(name = "direccion_envio", nullable = false)
    @NotBlank(message = "La dirección de envío es obligatoria")
    @Size(max=255)
    private String direccionEnvio;

    @Column(name = "codigo_cupon")
    @Size(max=255)
    private String codigoCupon;

    @Embedded
    private Audit audit = new Audit();

    // Relación One-to-Many con OrderItems
    @JsonManagedReference
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    public Order() {
        this.orderItems = new ArrayList<>();
    }

    // Helper methods para gestionar la relación bidireccional
    public void addOrderItem(OrderItem item) {
        orderItems.add(item);
        item.setOrder(this);
    }

    public void removeOrderItem(OrderItem item) {
        orderItems.remove(item);
        item.setOrder(null);
    }
}
