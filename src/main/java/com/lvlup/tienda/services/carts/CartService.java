package com.lvlup.tienda.services.carts;

import com.lvlup.tienda.models.carts.Cart;
import com.lvlup.tienda.models.carts.CartItem;
import com.lvlup.tienda.models.products.Product;
import com.lvlup.tienda.repositories.carts.CartItemRepository;
import com.lvlup.tienda.repositories.carts.CartRepository;
import com.lvlup.tienda.repositories.products.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    /**
     * Obtiene el carrito del usuario. Si no existe, lo crea.
     */
    @Transactional
    public Cart getOrCreateCartByUserId(Long userId) {
        Optional<Cart> existingCart = cartRepository.findByUserId(userId);

        if (existingCart.isPresent()) {
            return existingCart.get();
        }

        // Crear nuevo carrito si no existe
        Cart newCart = new Cart();
        newCart.setUserId(userId);
        return cartRepository.save(newCart);
    }

    /**
     * Agrega un producto al carrito o incrementa su cantidad si ya existe
     */
    @Transactional
    public Cart addProductToCart(Long userId, Long productId, Integer quantity) {
        // Verificar que el producto existe y tiene stock suficiente
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + productId));

        if (product.getStock() < quantity) {
            throw new RuntimeException("Stock insuficiente. Stock disponible: " + product.getStock());
        }

        // Obtener o crear el carrito
        Cart cart = getOrCreateCartByUserId(userId);

        // Verificar si el producto ya está en el carrito
        Optional<CartItem> existingItem = cart.getCartItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            // Si ya existe, incrementar la cantidad
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + quantity;

            // Verificar stock para la nueva cantidad
            if (product.getStock() < newQuantity) {
                throw new RuntimeException("Stock insuficiente para agregar más unidades. Stock disponible: " + product.getStock());
            }

            item.setQuantity(newQuantity);
            cartItemRepository.save(item);
        } else {
            // Si no existe, crear nuevo item
            CartItem newItem = new CartItem();
            newItem.setProductId(productId);
            newItem.setQuantity(quantity);
            newItem.setUnitPrice(BigDecimal.valueOf(product.getPrecio()));
            cart.addCartItem(newItem);
            cartItemRepository.save(newItem);
        }

        return cartRepository.save(cart);
    }

    /**
     * Actualiza la cantidad de un producto en el carrito
     */
    @Transactional
    public Cart updateCartItemQuantity(Long userId, Long cartItemId, Integer newQuantity) {
        Cart cart = getOrCreateCartByUserId(userId);

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Item del carrito no encontrado con ID: " + cartItemId));

        // Verificar que el item pertenece al carrito del usuario
        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("Este item no pertenece al carrito del usuario");
        }

        // Verificar stock disponible
        Product product = productRepository.findById(cartItem.getProductId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (product.getStock() < newQuantity) {
            throw new RuntimeException("Stock insuficiente. Stock disponible: " + product.getStock());
        }

        if (newQuantity <= 0) {
            // Si la nueva cantidad es 0 o menor, eliminar el item
            cart.removeCartItem(cartItem);
            cartItemRepository.delete(cartItem);
        } else {
            // Actualizar la cantidad
            cartItem.setQuantity(newQuantity);
            cartItemRepository.save(cartItem);
        }

        return cartRepository.save(cart);
    }

    /**
     * Elimina un producto del carrito
     */
    @Transactional
    public Cart removeProductFromCart(Long userId, Long cartItemId) {
        Cart cart = getOrCreateCartByUserId(userId);

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Item del carrito no encontrado con ID: " + cartItemId));

        // Verificar que el item pertenece al carrito del usuario
        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("Este item no pertenece al carrito del usuario");
        }

        cart.removeCartItem(cartItem);
        cartItemRepository.delete(cartItem);

        return cartRepository.save(cart);
    }

    /**
     * Limpia todo el carrito del usuario
     */
    @Transactional
    public void clearCart(Long userId) {
        Cart cart = getOrCreateCartByUserId(userId);
        cart.getCartItems().clear();
        cartRepository.save(cart);
    }

    /**
     * Calcula el total del carrito
     */
    public BigDecimal calculateCartTotal(Long userId) {
        Cart cart = getOrCreateCartByUserId(userId);

        return cart.getCartItems().stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Obtiene el carrito del usuario con todos sus items
     */
    @Transactional(readOnly = true)
    public Cart getCartByUserId(Long userId) {
        return getOrCreateCartByUserId(userId);
    }
}
