# LvlUp Backend - Tienda Online

Backend desarrollado con Spring Boot para la tienda online LvlUp, implementando autenticación JWT y gestión de productos, usuarios, órdenes y carritos de compra.

## Requisitos

- **Java 17 o superior**
- **Maven 3.6+**
- **PostgreSQL 12+** (AWS RDS)
- **IntelliJ IDEA** (recomendado)

## Tecnologías Utilizadas

- **Spring Boot 3.5.7**
- **Spring Security** con autenticación JWT
- **Spring Data JPA** para persistencia
- **PostgreSQL** como base de datos
- **Lombok** para reducir código boilerplate
- **Swagger/OpenAPI** para documentación de API
- **BCrypt** para encriptación de contraseñas
- **JJWT 0.12.6** para manejo de tokens JWT

## Configuración

### 1. Clonar el repositorio

```bash
git clone <url-del-repositorio>
cd lvlup-backend
```

### 2. Configurar la base de datos

La configuración de PostgreSQL ya está incluida en `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://database-appmovil.cxysktx9hnbc.us-east-1.rds.amazonaws.com:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=Duocvalpo.123
```

### 3. Inicializar roles en la base de datos

Los roles se crean automáticamente al iniciar la aplicación gracias al archivo `data.sql`. Los roles son:

- `ROLE_ADMIN` - Acceso total al sistema
- `ROLE_VENDEDOR` - Puede visualizar productos y órdenes
- `ROLE_CLIENTE` - Acceso a la tienda y gestión de carrito

**IMPORTANTE:** Si los roles no se crean automáticamente, ejecuta manualmente en PostgreSQL:

```sql
INSERT INTO roles (name) VALUES ('ROLE_ADMIN');
INSERT INTO roles (name) VALUES ('ROLE_VENDEDOR');
INSERT INTO roles (name) VALUES ('ROLE_CLIENTE');
```

### 4. Ejecutar la aplicación

#### Desde IntelliJ IDEA:
1. Abrir el proyecto en IntelliJ
2. Esperar a que Maven descargue las dependencias
3. Ejecutar la clase `TiendaApplication.java`

#### Desde terminal:
```bash
./mvnw spring-boot:run
```

La aplicación se iniciará en: `http://localhost:8080`

## Documentación de la API

Una vez iniciada la aplicación, la documentación Swagger estará disponible en:

**Swagger UI:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

**OpenAPI Docs:** [http://localhost:8080/api-docs](http://localhost:8080/api-docs)

## Autenticación

### Registro de usuario

```http
POST /api/v1/users/register
Content-Type: application/json

{
  "nombre": "Juan",
  "apellido": "Pérez",
  "correo": "juan@example.com",
  "contrasena": "password123",
  "rut": "12345678-9",
  "direccion": "Calle Falsa 123",
  "telefono": "+56912345678",
  "region": "Valparaíso",
  "comuna": "Valparaíso",
  "fechaNacimiento": 946684800000,
  "rol": "cliente"
}
```

### Login

```http
POST /login
Content-Type: application/json

{
  "correo": "juan@example.com",
  "contrasena": "password123"
}
```

**Respuesta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "juan@example.com",
  "message": "Autenticación exitosa para el usuario juan@example.com"
}
```

### Uso del token

Para todas las peticiones autenticadas, incluir el header:

```
Authorization: Bearer <token>
```

## Endpoints principales

### Usuarios
- `GET /api/v1/users` - Listar usuarios (ADMIN)
- `GET /api/v1/users/{id}` - Obtener usuario por ID
- `POST /api/v1/users/register` - Registrar nuevo usuario
- `PUT /api/v1/users/{id}` - Actualizar usuario
- `DELETE /api/v1/users/{id}` - Eliminar usuario (ADMIN)

### Productos
- `GET /api/v1/products` - Listar todos los productos (público)
- `GET /api/v1/products/{id}` - Obtener producto por ID (público)
- `GET /api/v1/products/categoria/{categoria}` - Filtrar por categoría
- `GET /api/v1/products/search?q=nombre` - Buscar por nombre
- `POST /api/v1/products` - Crear producto (ADMIN)
- `PUT /api/v1/products/{id}` - Actualizar producto (ADMIN)
- `DELETE /api/v1/products/{id}` - Eliminar producto (ADMIN)

### Órdenes
- `GET /api/v1/orders` - Listar órdenes (ADMIN, VENDEDOR)
- `GET /api/v1/orders/{id}` - Obtener orden por ID
- `POST /api/v1/orders` - Crear nueva orden (CLIENTE)
- `PUT /api/v1/orders/{id}` - Actualizar orden (ADMIN, VENDEDOR)

### Carritos
- `GET /api/v1/carts/user/{userId}` - Obtener carrito del usuario
- `POST /api/v1/carts` - Crear carrito
- `PUT /api/v1/carts/{id}` - Actualizar carrito
- `DELETE /api/v1/carts/{id}` - Eliminar carrito

## Roles y Permisos

| Rol | Permisos |
|-----|----------|
| **ADMIN** | Acceso total: CRUD de usuarios, productos, ver todas las órdenes |
| **VENDEDOR** | Ver productos y órdenes (solo lectura) |
| **CLIENTE** | Acceder a la tienda, gestionar carrito, crear órdenes |

## 🛠Estructura del Proyecto

```
src/main/java/com/lvlup/tienda/
├── controllers/         # Controladores REST
│   ├── users/
│   ├── products/
│   ├── orders/
│   └── carts/
├── models/              # Entidades JPA
│   ├── users/
│   ├── products/
│   ├── orders/
│   ├── carts/
│   ├── dtos/
│   └── audit/
├── repositories/        # Repositorios JPA
├── services/            # Lógica de negocio
├── security/            # Configuración de seguridad y JWT
│   └── filter/
├── exceptions/          # Manejo global de excepciones
└── TiendaApplication.java
```

## Testing

```bash
./mvnw test
```

## Build

```bash
./mvnw clean package
```

El archivo JAR se generará en `target/tienda-backend-0.0.1-SNAPSHOT.jar`

## Variables de Entorno (Producción)

Para producción, se recomienda usar variables de entorno:

```bash
export DB_URL=jdbc:postgresql://host:port/database
export DB_USERNAME=usuario
export DB_PASSWORD=contraseña
```

Y modificar `application.properties`:

```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
```

## Notas Importantes

- El token JWT expira después de 1 hora
- Las contraseñas se encriptan con BCrypt
- CORS está configurado para aceptar cualquier origen en desarrollo
- Para producción, configurar CORS con orígenes específicos

## Autores
- Desarrollo Fullstack II
- Evaluación Parcial N°3
- Carlos Muñoz
- Simon Villar


