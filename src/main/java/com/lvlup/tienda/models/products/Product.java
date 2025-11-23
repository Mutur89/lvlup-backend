package com.lvlup.tienda.models.products;

import com.lvlup.tienda.models.audit.Audit;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(max=255)
    private String nombre;

    @Column(nullable = false)
    @NotBlank(message = "La categoría del producto es obligatoria")
    @Size(max=255)
    private String categoria;

    @Column(length = 2000)
    @Size(max=2000)
    private String descripcion;

    @Column(nullable = false, length = 2000)
    @NotBlank(message = "La imagen del producto es obligatoria")
    @Size(max=2000)
    private String imagen;

    @Column(nullable = false)
    @NotNull(message = "El precio del producto es obligatorio")
    @Min(value = 0, message = "El precio debe ser mayor o igual a 0")
    private Integer precio;

    @Column(nullable = false)
    @NotNull(message = "El stock del producto es obligatorio")
    @Min(value = 0, message = "El stock debe ser mayor o igual a 0")
    private Integer stock;

    @Embedded
    private Audit audit = new Audit();
}
