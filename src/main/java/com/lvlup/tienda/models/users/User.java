package com.lvlup.tienda.models.users;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lvlup.tienda.models.audit.Audit;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Entity
@Table(name="users")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "El campo nombre es obligatorio")
    @Size(max=255)
    private String nombre;

    @Column(nullable = false)
    @NotBlank(message = "El campo apellido es obligatorio")
    @Size(max=255)
    private String apellido;

    @Column(unique = true, nullable = false)
    @NotBlank(message = "El campo correo es obligatorio")
    @Email(message = "El correo debe ser válido")
    @Size(max=255)
    private String correo;

    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank(message = "El campo contraseña es obligatorio")
    @Size(min=6, max=255)
    private String contrasena;

    @Column(nullable = false)
    @NotBlank(message = "El campo RUT es obligatorio")
    @Size(max=255)
    private String rut;

    @Column(nullable = false)
    @NotBlank(message = "El campo dirección es obligatorio")
    @Size(max=255)
    private String direccion;

    @Column(nullable = false)
    @NotBlank(message = "El campo teléfono es obligatorio")
    @Size(max=255)
    private String telefono;

    @Column(nullable = false)
    @NotBlank(message = "El campo región es obligatorio")
    @Size(max=255)
    private String region;

    @Column(nullable = false)
    @NotBlank(message = "El campo comuna es obligatorio")
    @Size(max=255)
    private String comuna;

    @Column(name = "fecha_nacimiento", nullable = false)
    private Long fechaNacimiento;

    @Column(nullable = false)
    @Size(max=255)
    private String rol;

    // Campo transitorio para indicar si es admin durante el registro
    @Transient
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private boolean admin;

    @Embedded
    private Audit audit = new Audit();

    // Relación Many-to-Many con roles
    @JsonIgnoreProperties({"users"})
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name="user_id"),
            inverseJoinColumns = @JoinColumn(name="role_id"),
            uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id","role_id"})}
    )
    private List<Role> roles;

    public User() {
        this.roles = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", correo='" + correo + '\'' +
                ", rol='" + rol + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(correo, user.correo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, correo);
    }
}
