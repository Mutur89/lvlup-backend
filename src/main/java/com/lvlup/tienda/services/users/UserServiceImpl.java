package com.lvlup.tienda.services.users;

import com.lvlup.tienda.models.users.Role;
import com.lvlup.tienda.models.users.User;
import com.lvlup.tienda.repositories.users.RoleRepository;
import com.lvlup.tienda.repositories.users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByCorreo(String correo) {
        return userRepository.findByCorreo(correo);
    }

    @Override
    @Transactional
    public User save(User user) {
        // 1. VALIDACIÓN MANUAL DE CONTRASEÑA (Solo para crear)
        // Se asegura que al crear un usuario nuevo, la contraseña esté presente
        if (user.getContrasena() == null || user.getContrasena().trim().isEmpty()) {
            throw new RuntimeException("La contraseña es obligatoria para crear un usuario");
        }

        // Obtener el rol del usuario desde el campo 'rol' de la entidad
        String roleName = user.getRol();
        if (roleName == null || roleName.isEmpty()) {
            roleName = "CLIENTE"; // Rol por defecto (Mayúsculas por seguridad)
        }

        // Mapear los roles según la convención de Spring Security
        List<Role> roles = new ArrayList<>();

        // Se fuerza mayúsculas para coincidir con los valores en BD y evitar errores de check constraint
        switch (roleName.toUpperCase()) {
            case "ADMIN":
                Role roleAdmin = roleRepository.findByName("ROLE_ADMIN")
                        .orElseThrow(() -> new RuntimeException("El rol ROLE_ADMIN no existe"));
                roles.add(roleAdmin);
                break;
            case "VENDEDOR":
                Role roleVendedor = roleRepository.findByName("ROLE_VENDEDOR")
                        .orElseThrow(() -> new RuntimeException("El rol ROLE_VENDEDOR no existe"));
                roles.add(roleVendedor);
                break;
            case "CLIENTE":
            default:
                Role roleCliente = roleRepository.findByName("ROLE_CLIENTE")
                        .orElseThrow(() -> new RuntimeException("El rol ROLE_CLIENTE no existe"));
                roles.add(roleCliente);
                break;
        }

        // Si el usuario es admin mediante el flag transitorio, agregar rol de admin
        if (user.isAdmin()) {
            Role roleAdmin = roleRepository.findByName("ROLE_ADMIN")
                    .orElseThrow(() -> new RuntimeException("El rol ROLE_ADMIN no existe"));
            if (!roles.contains(roleAdmin)) {
                roles.add(roleAdmin);
            }
        }

        user.setRoles(roles);

        // Encriptar la contraseña antes de guardar
        user.setContrasena(passwordEncoder.encode(user.getContrasena()));

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User update(Long id, User user) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));

        // --- ACTUALIZACIÓN INTELIGENTE ---
        // Solo actualizamos si el campo nuevo NO es null ni vacío.
        // De lo contrario, mantenemos el valor actual de la BD, evitando sobrescribir con nulls que violen constraints.

        if (user.getNombre() != null && !user.getNombre().isEmpty()) {
            existingUser.setNombre(user.getNombre());
        }
        if (user.getApellido() != null && !user.getApellido().isEmpty()) {
            existingUser.setApellido(user.getApellido());
        }
        if (user.getCorreo() != null && !user.getCorreo().isEmpty()) {
            existingUser.setCorreo(user.getCorreo());
        }
        if (user.getRut() != null && !user.getRut().isEmpty()) {
            existingUser.setRut(user.getRut());
        }
        
        // Campos opcionales que pueden tener restricción NOT NULL en BD
        // Aquí es donde evitamos el error de "comuna null"
        if (user.getDireccion() != null && !user.getDireccion().isEmpty()) {
            existingUser.setDireccion(user.getDireccion());
        }
        if (user.getTelefono() != null && !user.getTelefono().isEmpty()) {
            existingUser.setTelefono(user.getTelefono());
        }
        if (user.getRegion() != null && !user.getRegion().isEmpty()) {
            existingUser.setRegion(user.getRegion());
        }
        // Corrección específica para el error de "comuna":
        if (user.getComuna() != null && !user.getComuna().isEmpty()) {
            existingUser.setComuna(user.getComuna());
        }
        
        if (user.getFechaNacimiento() != null) {
            existingUser.setFechaNacimiento(user.getFechaNacimiento());
        }

        // Actualizar Rol (Si cambió y no es nulo)
        if (user.getRol() != null && !user.getRol().isEmpty() && !user.getRol().equals(existingUser.getRol())) {
             existingUser.setRol(user.getRol().toUpperCase()); // Forzamos mayúsculas
             
             // --- Lógica básica para actualizar la relación de roles si cambia el string ---
             List<Role> newRoles = new ArrayList<>();
             String roleKey = "ROLE_" + user.getRol().toUpperCase();
             // Mapeo simple, asumiendo que los nombres de roles coinciden con la convención
             if (roleKey.equals("ROLE_ADMIN") || roleKey.equals("ROLE_VENDEDOR") || roleKey.equals("ROLE_CLIENTE")) {
                 Role r = roleRepository.findByName(roleKey).orElse(null);
                 if (r != null) newRoles.add(r);
             } else {
                 // Fallback a cliente si el rol no es estándar
                 Role r = roleRepository.findByName("ROLE_CLIENTE").orElse(null);
                 if (r != null) newRoles.add(r);
             }
             existingUser.setRoles(newRoles);
        }

        // 2. LÓGICA DE CONTRASEÑA OPCIONAL (Solo para editar)
        if (user.getContrasena() != null && !user.getContrasena().trim().isEmpty()) {
            // Solo si viene una nueva contraseña, la encriptamos y guardamos
            existingUser.setContrasena(passwordEncoder.encode(user.getContrasena()));
        }
        // Si viene null o vacía, NO tocamos el campo, manteniendo la clave vieja.

        return userRepository.save(existingUser);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByCorreo(String correo) {
        return userRepository.existsByCorreo(correo);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByRut(String rut) {
        return userRepository.existsByRut(rut);
    }
}