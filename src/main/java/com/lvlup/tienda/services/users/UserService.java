package com.lvlup.tienda.services.users;

import com.lvlup.tienda.models.users.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> findAll();

    Optional<User> findById(Long id);

    Optional<User> findByCorreo(String correo);

    User save(User user);

    User update(Long id, User user);

    void delete(Long id);

    boolean existsByCorreo(String correo);

    boolean existsByRut(String rut);
}
