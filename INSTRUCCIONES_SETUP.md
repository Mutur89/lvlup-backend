# INSTRUCCIONES DE CONFIGURACIÓN FINAL

## **ANTES DE EJECUTAR EL BACKEND**

El backend web está configurado para trabajar con tu base de datos existente de la app móvil, pero necesitas realizar estos pasos **UNA SOLA VEZ**:

---

## **Paso 1: Ejecutar el script SQL**

1. Abre **PgAdmin4**
2. Conéctate a tu base de datos en AWS RDS
3. Abre el archivo `SETUP_DATABASE.sql` (está en la raíz del proyecto)
4. Ejecuta TODO el script

**Esto creará:**
- Tabla `roles`
- Tabla `users_roles` (relación Many-to-Many)
- Los 3 roles: ROLE_ADMIN, ROLE_VENDEDOR, ROLE_CLIENTE
- Migrará los roles existentes de tus usuarios

---

## **Paso 2: Verificar que el script funcionó**

Ejecuta esta query en PgAdmin4:

```sql
SELECT
    u.id,
    u.nombre,
    u.correo,
    u.rol as rol_app_movil,
    r.name as rol_backend_web
FROM users u
LEFT JOIN users_roles ur ON u.id = ur.user_id
LEFT JOIN roles r ON ur.role_id = r.id
ORDER BY u.id;
```

Deberías ver que cada usuario tiene:
- `rol_app_movil`: El rol original (admin, cliente, etc.)
- `rol_backend_web`: El nuevo rol (ROLE_ADMIN, ROLE_CLIENTE, etc.)

---

## **Paso 3: Abrir y ejecutar el proyecto en IntelliJ**

1. Abre IntelliJ IDEA
2. File → Open → Selecciona: `d:\Duoc\Fullstack 2\lvlup-backend`
3. Espera a que Maven descargue todas las dependencias (puede tardar unos minutos)
4. Una vez termine, ejecuta la aplicación:
   - Clic derecho en `TiendaApplication.java` → **Run 'TiendaApplication'**

---

## **Paso 4: Verificar que funciona**

Si todo salió bien, deberías ver en la consola:

```
Started TiendaApplication in X.XXX seconds
```

Y podrás acceder a:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/api-docs

---

## **Paso 5: Probar el Login**

### Opción A: Usar Swagger

1. Ve a http://localhost:8080/swagger-ui.html
2. Busca el endpoint `POST /login`
3. Click en "Try it out"
4. En el body, pon:
   ```json
   {
     "correo": "admin@admin.cl",
     "contrasena": "tu_contraseña"
   }
   ```
5. Click en "Execute"

### Opción B: Usar Postman o cURL

```bash
POST http://localhost:8080/login
Content-Type: application/json

{
  "correo": "tu_correo@example.com",
  "contrasena": "tu_contraseña"
}
```

**Respuesta esperada:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "tu_correo@example.com",
  "message": "Autenticación exitosa para el usuario tu_correo@example.com"
}
```

---

## **Paso 6: Usar el token en peticiones**

Para todas las peticiones autenticadas, incluye el header:

```
Authorization: Bearer <el_token_que_recibiste>
```

---

## **SISTEMA DUAL: App Móvil + Backend Web**

### Cómo funciona la compatibilidad:

| Sistema | Usa | Ubicación |
|---------|-----|-----------|
| **App Móvil (Kotlin)** | Campo `rol` en tabla users | `users.rol` (enum: ADMIN, CLIENTE) |
| **Backend Web (Java)** | Tablas `roles` y `users_roles` | Spring Security con roles |

**Ambos sistemas funcionan juntos sin conflictos:**
- La app móvil sigue usando `users.rol`
- El backend web usa `users_roles` para Spring Security
- Cuando creas un usuario desde el backend web, se actualiza **AMBOS**

---

## **Troubleshooting**

### Error: "Table 'roles' doesn't exist"
→ **Solución:** Ejecuta el script `SETUP_DATABASE.sql` en PgAdmin4

### Error: "Unable to build Hibernate SessionFactory"
→ **Solución:** Verifica que las tablas de la BD coincidan con las entidades Java. Revisa los logs para ver qué tabla o columna falta.

### Error: "Authentication failed"
→ **Solución:** Verifica que:
1. El usuario exista en la tabla `users`
2. La contraseña esté encriptada con BCrypt
3. El usuario tenga un rol asignado en `users_roles`

### El backend móvil deja de funcionar
→ **No debería pasar**, pero si ocurre:
1. Verifica que la columna `users.rol` siga existiendo
2. Verifica que las tablas `products`, `orders`, `carts` no se modificaron

---

## **Notas Importantes**

1. **ddl-auto está en `validate`** - Hibernate NO modificará tu BD, solo verificará que coincida
2. **Ambos backends pueden correr simultáneamente** - No hay conflictos
3. **Los passwords se encriptan con BCrypt** - No uses contraseñas en texto plano
4. **Los tokens JWT expiran en 1 hora** - Tendrás que hacer login de nuevo

---

## **Para la Evaluación**

Asegúrate de tener listos:
- Backend corriendo en puerto 8080
- Swagger documentando todos los endpoints
- Login funcionando con JWT
- Roles funcionando (ADMIN puede hacer CRUD, VENDEDOR solo lectura, CLIENTE usa tienda)
- Frontend React conectado al backend (próximo paso)

---

## **¿Problemas?**

Si algo no funciona:
1. Revisa los logs de IntelliJ (consola)
2. Verifica la conexión a la BD en PgAdmin4
3. Asegúrate de haber ejecutado el script SQL completo

---

**¡Listo para empezar a trabajar!** 
