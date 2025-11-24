package com.lvlup.tienda.security;

import com.lvlup.tienda.security.filter.JwtAuthenticationFilter;
import com.lvlup.tienda.security.filter.JwtValidationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class SpringSecurityConfig {

    @Autowired
    private AuthenticationConfiguration authenticationConfiguration;

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager() throws Exception {
        return this.authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests((authz) -> {
                    authz
                            // Rutas públicas
                            .requestMatchers(HttpMethod.POST, "/api/v1/users/register").permitAll()
                            .requestMatchers(HttpMethod.POST, "/login").permitAll()
                            .requestMatchers("/", "/swagger-ui/**", "/swagger-ui.html", "/api-docs/**", "/v3/api-docs/**").permitAll()

                            // Rutas de usuarios
                            .requestMatchers(HttpMethod.GET, "/api/v1/users").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.GET, "/api/v1/users/{id}").hasAnyRole("ADMIN", "VENDEDOR", "CLIENTE")
                            .requestMatchers(HttpMethod.POST, "/api/v1/users").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.PUT, "/api/v1/users/{id}").hasAnyRole("ADMIN", "CLIENTE")
                            .requestMatchers(HttpMethod.DELETE, "/api/v1/users/{id}").hasRole("ADMIN")

                            // Rutas de productos
                            .requestMatchers(HttpMethod.GET, "/api/v1/products", "/api/v1/products/**").permitAll()
                            .requestMatchers(HttpMethod.POST, "/api/v1/products").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.PUT, "/api/v1/products/{id}").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.DELETE, "/api/v1/products/{id}").hasRole("ADMIN")

                            // Rutas de órdenes (comentadas temporalmente hasta crear OrderController)
                            // .requestMatchers(HttpMethod.GET, "/api/v1/orders").hasAnyRole("ADMIN", "VENDEDOR")
                            // .requestMatchers(HttpMethod.GET, "/api/v1/orders/{id}").hasAnyRole("ADMIN", "VENDEDOR", "CLIENTE")
                            // .requestMatchers(HttpMethod.POST, "/api/v1/orders").hasRole("CLIENTE")
                            // .requestMatchers(HttpMethod.PUT, "/api/v1/orders/{id}").hasAnyRole("ADMIN", "VENDEDOR")

                            // Rutas de carritos (comentadas temporalmente hasta crear CartController)
                            // .requestMatchers(HttpMethod.GET, "/api/v1/carts/**").hasRole("CLIENTE")
                            // .requestMatchers(HttpMethod.POST, "/api/v1/carts/**").hasRole("CLIENTE")
                            // .requestMatchers(HttpMethod.PUT, "/api/v1/carts/**").hasRole("CLIENTE")
                            // .requestMatchers(HttpMethod.DELETE, "/api/v1/carts/**").hasRole("CLIENTE")

                            .anyRequest().authenticated();
                })
                .addFilter(new JwtAuthenticationFilter(authenticationManager()))
                .addFilter(new JwtValidationFilter(authenticationManager()))
                .csrf(config -> config.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(management ->
                        management.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(Arrays.asList("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
