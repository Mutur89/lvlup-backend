# API de Carrito de Compras - Documentación

## Base URL
```
http://localhost:8080/api/v1/carts
```

## Autenticación
Todos los endpoints requieren un token JWT válido en el header:
```
Authorization: Bearer <token>
```

---

## Endpoints

### 1. Obtener Carrito del Usuario

**GET** `/api/v1/carts`

Obtiene el carrito del usuario autenticado con todos sus items y el total.

#### Headers
```
Authorization: Bearer <token>
```

#### Respuesta Exitosa (200 OK)
```json
{
  "cart": {
    "id": 1,
    "userId": 5,
    "cartItems": [
      {
        "id": 1,
        "productId": 10,
        "quantity": 2,
        "unitPrice": 25990.00,
        "cartId": 1
      },
      {
        "id": 2,
        "productId": 15,
        "quantity": 1,
        "unitPrice": 45990.00,
        "cartId": 1
      }
    ]
  },
  "total": 97970.00,
  "itemCount": 2
}
```

---

### 2. Agregar Producto al Carrito

**POST** `/api/v1/carts/items`

Agrega un producto al carrito o incrementa su cantidad si ya existe.

#### Headers
```
Authorization: Bearer <token>
Content-Type: application/json
```

#### Body
```json
{
  "productId": 10,
  "quantity": 2
}
```

#### Respuesta Exitosa (200 OK)
```json
{
  "message": "Producto agregado al carrito exitosamente",
  "cart": {
    "id": 1,
    "userId": 5,
    "cartItems": [...]
  },
  "total": 97970.00
}
```

#### Errores Posibles
- **400 Bad Request**: Stock insuficiente, producto no encontrado
```json
{
  "error": "Stock insuficiente. Stock disponible: 5"
}
```

---

### 3. Actualizar Cantidad de un Item

**PUT** `/api/v1/carts/items/{cartItemId}`

Actualiza la cantidad de un producto específico en el carrito.

#### Headers
```
Authorization: Bearer <token>
Content-Type: application/json
```

#### Path Parameters
- `cartItemId`: ID del item del carrito a actualizar

#### Body
```json
{
  "quantity": 5
}
```

#### Respuesta Exitosa (200 OK)
```json
{
  "message": "Cantidad actualizada exitosamente",
  "cart": {
    "id": 1,
    "userId": 5,
    "cartItems": [...]
  },
  "total": 129950.00
}
```

#### Notas
- Si `quantity` es 0 o menor, el item se eliminará del carrito
- Se verifica que haya stock suficiente

---

### 4. Eliminar Producto del Carrito

**DELETE** `/api/v1/carts/items/{cartItemId}`

Elimina un producto específico del carrito.

#### Headers
```
Authorization: Bearer <token>
```

#### Path Parameters
- `cartItemId`: ID del item del carrito a eliminar

#### Respuesta Exitosa (200 OK)
```json
{
  "message": "Producto eliminado del carrito exitosamente",
  "cart": {
    "id": 1,
    "userId": 5,
    "cartItems": [...]
  },
  "total": 45990.00
}
```

---

### 5. Limpiar Carrito

**DELETE** `/api/v1/carts`

Elimina todos los productos del carrito del usuario.

#### Headers
```
Authorization: Bearer <token>
```

#### Respuesta Exitosa (200 OK)
```json
{
  "message": "Carrito limpiado exitosamente"
}
```

---

### 6. Calcular Total del Carrito

**GET** `/api/v1/carts/total`

Obtiene el total del carrito sin devolver los items.

#### Headers
```
Authorization: Bearer <token>
```

#### Respuesta Exitosa (200 OK)
```json
{
  "total": 97970.00
}
```

---

## Lógica de Negocio

### Validaciones

1. **Stock Disponible**
   - Al agregar productos, se verifica que haya stock suficiente
   - Al actualizar cantidades, se valida contra el stock actual

2. **Pertenencia del Item**
   - Solo se pueden modificar/eliminar items que pertenezcan al carrito del usuario autenticado

3. **Autocreación de Carrito**
   - Si el usuario no tiene carrito, se crea automáticamente al agregar el primer producto

### Cálculo de Total
```
Total = Σ (unitPrice × quantity) para cada item
```

### Unicidad de Productos
- Un producto solo puede aparecer UNA vez en el carrito
- Si se agrega un producto que ya existe, se incrementa su cantidad

---

## Ejemplo de Flujo Completo

### 1. Usuario hace login y obtiene token
```bash
POST /login
{
  "correo": "cliente@example.com",
  "contrasena": "password123"
}

# Respuesta:
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "cliente@example.com"
}
```

### 2. Agregar primer producto al carrito
```bash
POST /api/v1/carts/items
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
{
  "productId": 10,
  "quantity": 2
}
```

### 3. Agregar otro producto
```bash
POST /api/v1/carts/items
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
{
  "productId": 15,
  "quantity": 1
}
```

### 4. Ver carrito completo
```bash
GET /api/v1/carts
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### 5. Actualizar cantidad de un producto
```bash
PUT /api/v1/carts/items/1
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
{
  "quantity": 5
}
```

### 6. Eliminar un producto
```bash
DELETE /api/v1/carts/items/2
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### 7. Limpiar todo el carrito
```bash
DELETE /api/v1/carts
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

## Swagger UI

Puedes probar todos estos endpoints en:
```
http://localhost:8080/swagger-ui.html
```

---

## Base de Datos

### Tablas Creadas

#### `carts`
| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | BIGSERIAL | ID del carrito (PK) |
| user_id | BIGINT | ID del usuario propietario (FK) |

#### `cart_items`
| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | BIGSERIAL | ID del item (PK) |
| cart_id | BIGINT | ID del carrito (FK) |
| product_id | BIGINT | ID del producto (FK) |
| quantity | INTEGER | Cantidad del producto |
| unit_price | DECIMAL(10,2) | Precio unitario |

### Ejecutar Script SQL
```bash
# Conectarse a PostgreSQL
psql -h database-appmovil.cxysktx9hnbc.us-east-1.rds.amazonaws.com -U postgres -d postgres

# Ejecutar script
\i src/main/resources/db/migration/create_cart_tables.sql
```

---

## Seguridad

- Todos los endpoints requieren autenticación JWT
- Los usuarios solo pueden acceder a su propio carrito
- Roles permitidos: ADMIN, VENDEDOR, CLIENTE
- El carrito se identifica automáticamente por el usuario autenticado

---

## Notas Importantes

1. **Persistencia**: El carrito se guarda en base de datos, no en sesión
2. **Multi-dispositivo**: El mismo carrito es accesible desde cualquier dispositivo
3. **Stock Real-time**: Se valida contra el stock actual en cada operación
4. **Precio Histórico**: Se guarda el precio al momento de agregar al carrito
5. **Cascada**: Si se elimina un usuario o producto, los items relacionados se eliminan automáticamente
