package com.lvlup.tienda.controllers.carts;

import com.lvlup.tienda.models.carts.Cart;
import com.lvlup.tienda.models.users.User;
import com.lvlup.tienda.repositories.users.UserRepository;
import com.lvlup.tienda.services.carts.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/carts")
@Tag(name = "Carritos", description = "API para gestión de carritos de compras")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Obtiene el carrito del usuario autenticado
     */
    @GetMapping
    @Operation(summary = "Obtener carrito", description = "Obtiene el carrito del usuario autenticado con todos sus items")
    public ResponseEntity<?> getMyCart(Authentication authentication) {
        try {
            Long userId = getUserIdFromAuthentication(authentication);
            Cart cart = cartService.getCartByUserId(userId);
            BigDecimal total = cartService.calculateCartTotal(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("cart", cart);
            response.put("total", total);
            response.put("itemCount", cart.getCartItems().size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener el carrito: " + e.getMessage()));
        }
    }

    /**
     * Agrega un producto al carrito
     */
    @PostMapping("/items")
    @Operation(summary = "Agregar producto", description = "Agrega un producto al carrito o incrementa su cantidad si ya existe")
    public ResponseEntity<?> addProductToCart(
            @RequestBody AddToCartRequest request,
            Authentication authentication) {
        try {
            Long userId = getUserIdFromAuthentication(authentication);

            Cart cart = cartService.addProductToCart(
                    userId,
                    request.getProductId(),
                    request.getQuantity() != null ? request.getQuantity() : 1
            );

            BigDecimal total = cartService.calculateCartTotal(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Producto agregado al carrito exitosamente");
            response.put("cart", cart);
            response.put("total", total);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al agregar producto al carrito: " + e.getMessage()));
        }
    }

    /**
     * Actualiza la cantidad de un item del carrito
     */
    @PutMapping("/items/{cartItemId}")
    @Operation(summary = "Actualizar cantidad", description = "Actualiza la cantidad de un producto en el carrito")
    public ResponseEntity<?> updateCartItemQuantity(
            @PathVariable Long cartItemId,
            @RequestBody UpdateQuantityRequest request,
            Authentication authentication) {
        try {
            Long userId = getUserIdFromAuthentication(authentication);

            Cart cart = cartService.updateCartItemQuantity(
                    userId,
                    cartItemId,
                    request.getQuantity()
            );

            BigDecimal total = cartService.calculateCartTotal(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Cantidad actualizada exitosamente");
            response.put("cart", cart);
            response.put("total", total);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al actualizar cantidad: " + e.getMessage()));
        }
    }

    /**
     * Elimina un producto del carrito
     */
    @DeleteMapping("/items/{cartItemId}")
    @Operation(summary = "Eliminar producto", description = "Elimina un producto específico del carrito")
    public ResponseEntity<?> removeProductFromCart(
            @PathVariable Long cartItemId,
            Authentication authentication) {
        try {
            Long userId = getUserIdFromAuthentication(authentication);

            Cart cart = cartService.removeProductFromCart(userId, cartItemId);
            BigDecimal total = cartService.calculateCartTotal(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Producto eliminado del carrito exitosamente");
            response.put("cart", cart);
            response.put("total", total);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al eliminar producto del carrito: " + e.getMessage()));
        }
    }

    /**
     * Limpia todo el carrito
     */
    @DeleteMapping
    @Operation(summary = "Limpiar carrito", description = "Elimina todos los productos del carrito")
    public ResponseEntity<?> clearCart(Authentication authentication) {
        try {
            Long userId = getUserIdFromAuthentication(authentication);
            cartService.clearCart(userId);

            return ResponseEntity.ok(Map.of("message", "Carrito limpiado exitosamente"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al limpiar el carrito: " + e.getMessage()));
        }
    }

    /**
     * Obtiene el total del carrito
     */
    @GetMapping("/total")
    @Operation(summary = "Calcular total", description = "Calcula el total del carrito del usuario")
    public ResponseEntity<?> getCartTotal(Authentication authentication) {
        try {
            Long userId = getUserIdFromAuthentication(authentication);
            BigDecimal total = cartService.calculateCartTotal(userId);

            return ResponseEntity.ok(Map.of("total", total));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al calcular el total: " + e.getMessage()));
        }
    }

    /**
     * Helper method para obtener el ID del usuario autenticado
     */
    private Long getUserIdFromAuthentication(Authentication authentication) {
        String correo = authentication.getName();
        User user = userRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return user.getId();
    }

    // DTOs internos para los requests

    public static class AddToCartRequest {
        private Long productId;
        private Integer quantity;

        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
    }

    public static class UpdateQuantityRequest {
        private Integer quantity;

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
    }
}
