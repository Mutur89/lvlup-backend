-- Script de inicialización para roles
-- Este script se ejecuta automáticamente al iniciar la aplicación

-- Insertar roles si no existen
INSERT INTO roles (name) VALUES ('ROLE_ADMIN') ON CONFLICT DO NOTHING;
INSERT INTO roles (name) VALUES ('ROLE_VENDEDOR') ON CONFLICT DO NOTHING;
INSERT INTO roles (name) VALUES ('ROLE_CLIENTE') ON CONFLICT DO NOTHING;
