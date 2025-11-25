-- Script SQL para crear las tablas de carritos de compra
-- Ejecutar este script en la base de datos PostgreSQL

-- 1. Crear tabla carts
CREATE TABLE IF NOT EXISTS carts (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_cart_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Índice para mejorar las búsquedas por user_id
CREATE INDEX IF NOT EXISTS idx_cart_user_id ON carts(user_id);

-- 2. Crear tabla cart_items
CREATE TABLE IF NOT EXISTS cart_items (
    id BIGSERIAL PRIMARY KEY,
    cart_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    unit_price DECIMAL(10, 2) NOT NULL CHECK (unit_price >= 0),
    CONSTRAINT fk_cart_item_cart FOREIGN KEY (cart_id) REFERENCES carts(id) ON DELETE CASCADE,
    CONSTRAINT fk_cart_item_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

-- Índice para mejorar las búsquedas por cart_id
CREATE INDEX IF NOT EXISTS idx_cart_item_cart_id ON cart_items(cart_id);

-- Índice para mejorar las búsquedas por product_id
CREATE INDEX IF NOT EXISTS idx_cart_item_product_id ON cart_items(product_id);

-- Restricción única para evitar duplicados del mismo producto en un carrito
CREATE UNIQUE INDEX IF NOT EXISTS idx_cart_item_unique ON cart_items(cart_id, product_id);

-- Comentarios para documentación
COMMENT ON TABLE carts IS 'Tabla que almacena los carritos de compra de los usuarios';
COMMENT ON TABLE cart_items IS 'Tabla que almacena los items (productos) dentro de cada carrito';
COMMENT ON COLUMN carts.user_id IS 'ID del usuario propietario del carrito';
COMMENT ON COLUMN cart_items.cart_id IS 'ID del carrito al que pertenece este item';
COMMENT ON COLUMN cart_items.product_id IS 'ID del producto en este item';
COMMENT ON COLUMN cart_items.quantity IS 'Cantidad del producto en el carrito';
COMMENT ON COLUMN cart_items.unit_price IS 'Precio unitario del producto al momento de agregarlo al carrito';
