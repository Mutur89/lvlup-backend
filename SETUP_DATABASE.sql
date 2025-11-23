-- =====================================================
-- SCRIPT DE CONFIGURACIÓN DE BASE DE DATOS
-- Para hacer compatible el backend web con la BD existente
-- =====================================================

-- IMPORTANTE: Ejecutar este script UNA SOLA VEZ en PgAdmin4
-- antes de iniciar el backend web por primera vez

-- =====================================================
-- 1. CREAR TABLA ROLES (para Spring Security)
-- =====================================================

CREATE TABLE IF NOT EXISTS roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

-- =====================================================
-- 2. INSERTAR LOS 3 ROLES NECESARIOS
-- =====================================================

INSERT INTO roles (name) VALUES ('ROLE_ADMIN') ON CONFLICT (name) DO NOTHING;
INSERT INTO roles (name) VALUES ('ROLE_VENDEDOR') ON CONFLICT (name) DO NOTHING;
INSERT INTO roles (name) VALUES ('ROLE_CLIENTE') ON CONFLICT (name) DO NOTHING;

-- =====================================================
-- 3. CREAR TABLA INTERMEDIA users_roles
-- =====================================================

CREATE TABLE IF NOT EXISTS users_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- =====================================================
-- 4. MIGRAR ROLES EXISTENTES DE USUARIOS
-- (Asignar roles en users_roles basándose en el campo 'rol')
-- =====================================================

-- Asignar ROLE_ADMIN a usuarios con rol='admin' o rol='ADMIN'
INSERT INTO users_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
CROSS JOIN roles r
WHERE r.name = 'ROLE_ADMIN'
  AND UPPER(u.rol) = 'ADMIN'
  AND NOT EXISTS (
      SELECT 1 FROM users_roles ur
      WHERE ur.user_id = u.id AND ur.role_id = r.id
  );

-- Asignar ROLE_VENDEDOR a usuarios con rol='vendedor' o rol='VENDEDOR'
INSERT INTO users_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
CROSS JOIN roles r
WHERE r.name = 'ROLE_VENDEDOR'
  AND UPPER(u.rol) = 'VENDEDOR'
  AND NOT EXISTS (
      SELECT 1 FROM users_roles ur
      WHERE ur.user_id = u.id AND ur.role_id = r.id
  );

-- Asignar ROLE_CLIENTE a usuarios con rol='cliente' o rol='CLIENTE'
INSERT INTO users_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
CROSS JOIN roles r
WHERE r.name = 'ROLE_CLIENTE'
  AND UPPER(u.rol) = 'CLIENTE'
  AND NOT EXISTS (
      SELECT 1 FROM users_roles ur
      WHERE ur.user_id = u.id AND ur.role_id = r.id
  );

-- =====================================================
-- 5. VERIFICAR LOS DATOS
-- =====================================================

-- Ver todos los roles creados
SELECT * FROM roles;

-- Ver las asignaciones de roles a usuarios
SELECT
    u.id,
    u.nombre,
    u.correo,
    u.rol as rol_movil,
    r.name as rol_web
FROM users u
LEFT JOIN users_roles ur ON u.id = ur.user_id
LEFT JOIN roles r ON ur.role_id = r.id
ORDER BY u.id;

-- =====================================================
-- FIN DEL SCRIPT
-- =====================================================

-- NOTAS IMPORTANTES:
-- 1. La columna 'rol' en users se mantiene para compatibilidad con app móvil
-- 2. La tabla 'users_roles' es usada por el backend web (Spring Security)
-- 3. Ambos sistemas pueden convivir sin conflictos
-- 4. Los nuevos usuarios creados desde el backend web tendrán ambos:
--    - Campo 'rol' (para app móvil)
--    - Relación en 'users_roles' (para backend web)
