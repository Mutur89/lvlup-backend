package com.lvlup.tienda.services.users;

import com.lvlup.tienda.models.users.User;
import com.lvlup.tienda.repositories.users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JpaUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        User user = userRepository.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con correo: " + correo));

        // Lista para acumular todas las autoridades (Roles + Permisos)
        List<GrantedAuthority> authorities = new ArrayList<>();

        // 1. Agregar los Roles como autoridades (ej: "ROLE_ADMIN")
        authorities.addAll(
                user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList())
        );

        // 2. Agregar los Permisos de cada Rol como autoridades (ej: "PRODUCT_CREATE")
        user.getRoles().forEach(role -> {
            if (role.getPermissions() != null) {
                role.getPermissions().forEach(permission -> {
                    authorities.add(new SimpleGrantedAuthority(permission.getName()));
                });
            }
        });

        // Retornar UserDetails de Spring Security con la lista completa
        return new org.springframework.security.core.userdetails.User(
                user.getCorreo(),
                user.getContrasena(),
                true, // enabled
                true, // accountNonExpired
                true, // credentialsNonExpired
                true, // accountNonLocked
                authorities
        );
    }
}