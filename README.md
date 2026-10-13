# Nexo Ecommerce

El proyecto usa Eureka para descubrir servicios y Spring Gateway como entrada
HTTP. La estructura de arranque con Docker Compose sigue la práctica del ZIP,
con los servicios propios de Nexo: Auth, Orders y Catalog.

## Arranque con Docker

Desde esta carpeta, genera una clave JWT y arranca los contenedores:

```powershell
$rng = [Security.Cryptography.RandomNumberGenerator]::Create()
$jwtBytes = New-Object byte[] 32
$rng.GetBytes($jwtBytes)
$env:JWT_SECRET = [Convert]::ToBase64String($jwtBytes)
$rng.Dispose()
docker compose up --build -d
```

Conserva la misma clave JWT al reiniciar los servicios. Si quieres crear la
cuenta administrativa inicial, define `AUTH_ADMIN_EMAIL` y
`AUTH_ADMIN_PASSWORD` antes de ejecutar Compose; el registro público solo crea
cuentas de cliente.

| Servicio | Dirección local |
| --- | --- |
| Eureka | `http://127.0.0.1:8761` |
| Gateway | `http://127.0.0.1:8082` |
| RabbitMQ Management | `http://127.0.0.1:15672` |

Angular puede ejecutarse aparte desde `web` con `npm start`. Su autenticación
de desarrollo ya llama al Gateway; las pantallas de catálogo y pedidos todavía
usan datos locales en el frontend. Las rutas HTTP disponibles son
`/api/auth/**`, `/api/orders/**`, `/api/catalog/**` y
`/api/webhooks/stripe`.

Auth usa PostgreSQL y Redis; Orders usa otro PostgreSQL y RabbitMQ; Catalog
usa su propio PostgreSQL. Para usar pagos reales, configura `STRIPE_API_KEY` y
`STRIPE_WEBHOOK_SECRET`. La carga de imágenes de Catalog requiere configurar
`AWS_S3_BUCKET`, `CDN_URL` y credenciales AWS. El Gateway y los endpoints de
Orders se mantienen limitados al entorno local mientras Orders no valide la
identidad del usuario.

El archivo Compose raíz no reemplaza los Compose individuales de `services/auth`
y `services/orders`, que siguen disponibles para arrancar solo sus dependencias
durante el desarrollo.
