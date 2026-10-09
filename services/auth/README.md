# Servicio de autenticación

Microservicio Spring Boot que implementa autenticación siguiendo DDD y
Arquitectura Limpia. El dominio y los casos de uso no dependen de Spring,
persistencia, JWT, Redis ni de la capa de presentación. Los adaptadores de
infraestructura implementan los puertos de aplicación y ArchUnit verifica estas
reglas de dependencia.

## Funcionalidades

- `POST /auth/register`: crea una cuenta con rol `Client`, guarda la contraseña
  con BCrypt y devuelve un JWT firmado.
- `POST /auth/login`: valida las credenciales y devuelve un JWT firmado.
- `GET /auth/me`: devuelve el perfil público del usuario autenticado. Requiere
  `Authorization: Bearer <token>`.
- `POST /auth/logout`: revoca el JWT en Redis hasta su fecha de expiración y
  responde `204 No Content`.
- El registro público nunca permite escoger el rol `Admin`.
- Un administrador se provisiona de forma controlada mediante variables de
  entorno; no existe una contraseña administrativa predeterminada en el código.

El contrato HTTP se mantiene en `../../docs/openapi/auth-service.yaml`.

## Iniciar en Windows

Abre una terminal de PowerShell en esta carpeta. Inicia PostgreSQL y Redis:

```powershell
docker compose up -d
```

Configura el secreto de firma JWT y las credenciales del administrador inicial.
La contraseña debe tener al menos 8 caracteres; para mayor seguridad, usa una
frase larga y única. Este método para generar la clave JWT funciona en Windows
PowerShell:

```powershell
$rng = [Security.Cryptography.RandomNumberGenerator]::Create()
$jwtBytes = New-Object byte[] 32
$rng.GetBytes($jwtBytes)
$env:JWT_SECRET = [Convert]::ToBase64String($jwtBytes)
$rng.Dispose()
$env:AUTH_ADMIN_NAME = "Administrador Nexo"
$env:AUTH_ADMIN_EMAIL = "admin@tudominio.local"
$env:AUTH_ADMIN_PASSWORD = "CAMBIA-ESTA-POR-UNA-CLAVE-SEGURA"
```

Arranca el servicio:

```powershell
..\orders\mvnw.cmd spring-boot:run
```

Mantén la terminal abierta. El servicio escucha en `http://localhost:8084`.
PostgreSQL usa por defecto `localhost:5433` y Redis `localhost:6379`. Flyway
crea la tabla de usuarios al iniciar el servicio.

El bootstrap del administrador es idempotente: si esa cuenta ya existe como
administrador, no cambia su contraseña; si el correo pertenece a una cuenta
normal, el servicio falla explícitamente en vez de elevar sus privilegios.
Para cambiar la contraseña del administrador existente, utiliza un proceso
administrativo controlado; no uses el registro público para asignar roles.

La clave `JWT_SECRET` debe mantenerse secreta y estable entre reinicios. No la
guardes en el repositorio ni compartas las credenciales de administrador. Los
valores locales predeterminados de PostgreSQL son solo para desarrollo; en
producción utiliza un gestor de secretos, credenciales seguras y Redis protegido
con autenticación y TLS.

Puedes cambiar configuración mediante `DATABASE_URL`, `DATABASE_USERNAME`,
`DATABASE_PASSWORD`, `REDIS_HOST`, `REDIS_PORT`, `REDIS_PASSWORD`,
`JWT_EXPIRATION`, `JWT_ISSUER` y `AUTH_CORS_ALLOWED_ORIGINS`. El origen permitido
por defecto para Angular es `http://localhost:4200`.

## Administrador inicial y roles

El administrador se crea al arrancar Auth únicamente si están definidas juntas
`AUTH_ADMIN_EMAIL` y `AUTH_ADMIN_PASSWORD`. El nombre se puede configurar con
`AUTH_ADMIN_NAME`. La contraseña se almacena con BCrypt; nunca se devuelve en
las respuestas ni se imprime en los registros. La cuenta recibe el rol `Admin`,
que la pantalla Angular utiliza para habilitar el área de administración.

El backend no eleva cuentas existentes. Si el correo de bootstrap ya pertenece
a un usuario `Client`, el arranque falla y requiere resolver explícitamente esa
colisión en la base; no se concede el rol de administrador a una cuenta pública.

## Pruebas

Ejecuta desde esta carpeta las pruebas unitarias de casos de uso, las reglas
arquitectónicas y las pruebas de integración:

```powershell
..\orders\mvnw.cmd verify
```

Docker debe estar activo para que Testcontainers arranque PostgreSQL y Redis
aislados. Las pruebas de integración cubren registro, usuario duplicado, login,
consulta `/me`, logout/revocación, endpoint protegido y el acceso administrativo.

## Ejemplos HTTP

Registro de un cliente:

```http
POST http://localhost:8084/auth/register
Content-Type: application/json

{
  "name": "Usuario Nexo",
  "email": "usuario@example.com",
  "password": "contrasena-segura-123"
}
```

Inicio de sesión:

```http
POST http://localhost:8084/auth/login
Content-Type: application/json

{
  "email": "usuario@example.com",
  "password": "contrasena-segura-123"
}
```

Consulta del perfil y cierre de sesión:

```http
Authorization: Bearer <token>
```

El JWT expira en 15 minutos por defecto. Cambia ese periodo mediante
`JWT_EXPIRATION`.
