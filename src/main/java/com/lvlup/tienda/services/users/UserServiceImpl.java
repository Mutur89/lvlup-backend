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
        // Obtener el rol del usuario desde el campo 'rol' de la entidad
        String roleName = user.getRol();
        if (roleName == null || roleName.isEmpty()) {
            roleName = "cliente"; // Rol por defecto
        }

        // Mapear los roles según la convención de Spring Security
        List<Role> roles = new ArrayList<>();

        switch (roleName.toLowerCase()) {
            case "admin":
                Role roleAdmin = roleRepository.findByName("ROLE_ADMIN")
                        .orElseThrow(() -> new RuntimeException("El rol ROLE_ADMIN no existe"));
                roles.add(roleAdmin);
                break;
            case "vendedor":
                Role roleVendedor = roleRepository.findByName("ROLE_VENDEDOR")
                        .orElseThrow(() -> new RuntimeException("El rol ROLE_VENDEDOR no existe"));
                roles.add(roleVendedor);
                break;
            case "cliente":
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

        // Encriptar la contraseña
        user.setContrasena(passwordEncoder.encode(user.getContrasena()));

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User update(Long id, User user) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));

        // Actualizar campos
        existingUser.setNombre(user.getNombre());
        existingUser.setApellido(user.getApellido());
        existingUser.setCorreo(user.getCorreo());
        existingUser.setRut(user.getRut());
        existingUser.setDireccion(user.getDireccion());
        existingUser.setTelefono(user.getTelefono());
        existingUser.setRegion(user.getRegion());
        existingUser.setComuna(user.getComuna());
        existingUser.setFechaNacimiento(user.getFechaNacimiento());
        existingUser.setRol(user.getRol());

        // Si se proporciona una nueva contraseña, encriptarla
        if (user.getContrasena() != null && !user.getContrasena().isEmpty()) {
            existingUser.setContrasena(passwordEncoder.encode(user.getContrasena()));
        }

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
