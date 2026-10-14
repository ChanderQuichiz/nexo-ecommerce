# Nexo Ecommerce

Aplicación de comercio electrónico con Angular, Spring Boot, Eureka, Gateway,
PostgreSQL, Redis, RabbitMQ, Stripe y Notifications.

## Requisitos

- Docker Desktop
- Node.js 20 o superior
- npm
- Stripe CLI
- Una cuenta de Stripe en modo prueba

## Configuración local

Desde la raíz del proyecto, usa [`.env`](./.env). Este archivo está ignorado
por Git y contiene los valores locales de desarrollo.

Variables importantes:

```env
JWT_SECRET=una-clave-Base64-de-al-menos-32-bytes
AUTH_ADMIN_EMAIL=admin@example.com
AUTH_ADMIN_PASSWORD=una-clave-segura
STRIPE_API_KEY=sk_test_...
STRIPE_WEBHOOK_SECRET=whsec_...
AUTH_CORS_ALLOWED_ORIGINS=https://tu-frontend.example.com
GATEWAY_CORS_ALLOWED_ORIGINS=https://tu-frontend.example.com
```

Usa [`.env.example`](./.env.example) como plantilla. En producción, inyecta
los secretos con el gestor de secretos de tu plataforma; no uses los valores
por defecto de desarrollo. `AUTH_ADMIN_NAME`, `AUTH_ADMIN_EMAIL` y
`AUTH_ADMIN_PASSWORD` solo se utilizan para crear o actualizar el usuario
administrador inicial. Cambiar esas variables no cambia las credenciales de
usuarios ya existentes.

La plantilla contiene también las variables opcionales que tienen valores por
defecto en Spring Boot, para que puedas trasladar el inventario completo a ECS.
Las variables con nombres `*_ROUTE_URI`, `EUREKA_URL`, `ORDERS_SERVICE_URL` y
`AUTH_SERVICE_URL` deben apuntar a los nombres DNS o Service Connect de ECS
cuando no uses la red local de Docker Compose.

`AUTH_CORS_ALLOWED_ORIGINS` se consume por Auth y
`GATEWAY_CORS_ALLOWED_ORIGINS` por Gateway. Son listas separadas por comas y
deben contener únicamente los orígenes reales del frontend, incluyendo
protocolo y puerto cuando corresponda; no agregues `/` al final.

`CURRENCY` es configuración de negocio, no un secreto, y se mantiene como
variable de entorno porque afecta los pagos y los comprobantes. Actualmente
Orders usa envío gratuito como una constante del dominio y no existe todavía
un flujo de intereses; por eso no se configuran `SHIPPING_FEE` ni
`INTEREST_RATE` en `.env`.

El frontend se publica como archivos estáticos en S3 detrás de CloudFront.
Angular no puede leer variables de ECS ni `.env` después de compilarse, por lo
que debes generar su configuración antes del build:

```bash
cd web
npm run build:production
```

El comando carga automáticamente [`.env.production`](./.env.production).
Para desarrollo local usa:

```bash
cd web
npm run build:local
```

que carga [`.env`](./.env). `.env` y `.env.production` están ignorados por
Git. En CI/CD, configura las variables de `.env.production` como
variables/secrets del pipeline; no subas `.env.production`.

El comando genera `public/runtime-config.js` y después crea `dist/web`, que es
la carpeta que debes subir a S3. Para desarrollo, `npm run build` usa `/api`
como URL predeterminada y no requiere una clave Stripe. Nunca coloques
`STRIPE_API_KEY`, `STRIPE_WEBHOOK_SECRET`, `JWT_SECRET`, credenciales AWS,
contraseñas de base de datos o SMTP en el código del frontend.

Configura en CloudFront una respuesta de error `403` y otra `404` que devuelvan
`/index.html` con estado `200`, para que funcionen las rutas de Angular.

### Variables por Task Definition de ECS

El archivo [`.env.example`](./.env.example) está comentado por tarea. En ECS
no copies automáticamente todo el archivo en cada task: agrega únicamente las
variables de la tarea correspondiente. Las variables `AUTH_DATABASE_*`,
`ORDERS_DATABASE_*` y `CATALOG_DATABASE_*` son nombres auxiliares de Compose;
en cada task de Spring se inyectan como `DATABASE_URL`,
`DATABASE_USERNAME` y `DATABASE_PASSWORD`.

| Task Definition | Variables principales |
| --- | --- |
| `discovery` | `DISCOVERY_BIND_ADDRESS`, `DISCOVERY_PORT` |
| `gateway` | `GATEWAY_BIND_ADDRESS`, `GATEWAY_PORT`, `GATEWAY_CORS_ALLOWED_ORIGINS`, `EUREKA_URL`, `AUTH_ROUTE_URI`, `ORDERS_ROUTE_URI`, `CATALOG_ROUTE_URI`, `NOTIFICATIONS_ROUTE_URI` |
| `auth` | `SERVER_PORT`, `EUREKA_URL`, `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD`, `REDIS_HOST`, `REDIS_PORT`, `REDIS_PASSWORD`, `INTERNAL_API_KEY`, `JWT_SECRET`, `JWT_EXPIRATION`, `JWT_ISSUER`, `AUTH_CORS_ALLOWED_ORIGINS`, `AUTH_ADMIN_NAME`, `AUTH_ADMIN_EMAIL`, `AUTH_ADMIN_PASSWORD` |
| `orders` | `EUREKA_URL`, `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD`, `RABBITMQ_HOST`, `RABBITMQ_PORT`, `RABBITMQ_USERNAME`, `RABBITMQ_PASSWORD`, `CURRENCY`, `STRIPE_API_KEY`, `STRIPE_WEBHOOK_SECRET` |
| `catalog` | `EUREKA_SERVER_URL`, `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD`, `SERVER_PORT`, `AWS_REGION`, `AWS_S3_BUCKET`, `CDN_URL`, `AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY` |
| `notifications` | `SERVER_PORT`, `EUREKA_SERVER_URL`, `EUREKA_CLIENT_ENABLED`, `RABBITMQ_HOST`, `RABBITMQ_PORT`, `RABBITMQ_USERNAME`, `RABBITMQ_PASSWORD`, `ORDERS_SERVICE_URL`, `AUTH_SERVICE_URL`, `INTERNAL_API_KEY`, `RECEIPT_CURRENCY`, `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD`, `MAIL_SMTP_AUTH`, `MAIL_STARTTLS_ENABLED`, `MAIL_FROM` y variables `RABBITMQ_RETRY_*` |

`WEB_API_URL` y `STRIPE_PUBLIC_KEY` no pertenecen a ninguna task de ECS:
se usan únicamente al compilar Angular antes de subir `dist/web` a S3.

Para Gmail SMTP:

```env
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=tu-cuenta@gmail.com
MAIL_PASSWORD=tu-contraseña-de-aplicacion
MAIL_SMTP_AUTH=true
MAIL_STARTTLS_ENABLED=true
MAIL_FROM=tu-cuenta@gmail.com
```

`MAIL_USERNAME` y `MAIL_PASSWORD` pertenecen a la cuenta que envía los
correos. El destinatario se obtiene del usuario de la orden y no se coloca en
`.env`. `MAIL_PASSWORD` debe ser una App Password de Google, no la contraseña
normal de Gmail.

## Levantar el backend

Desde la raíz:

```bash
docker compose up -d --build
```

Para comprobar los servicios:

```bash
docker compose ps
```

Direcciones locales:

| Servicio | URL |
| --- | --- |
| Frontend Angular | <http://localhost:4200> |
| Gateway | <http://localhost:8082> |
| Eureka | <http://localhost:8761> |
| RabbitMQ Management | <http://localhost:15672> |

El frontend utiliza el Gateway mediante:

```text
http://localhost:8082/api
```

## Instalar Stripe CLI

### macOS

Con Homebrew:

```bash
brew install stripe/stripe-cli/stripe
stripe login
```

El comando `stripe login` abre el navegador para autorizar la CLI. Usa la
misma cuenta de Stripe cuyo `STRIPE_API_KEY` está configurado en [`.env`](./.env).

### Windows

Instala Stripe CLI desde la documentación oficial:
<https://docs.stripe.com/stripe-cli>

Después ejecuta:

```powershell
stripe login
```

## Configurar el webhook local

Primero carga las variables del archivo `.env` en la terminal y ejecuta Stripe
CLI desde la raíz del proyecto:

```bash
set -a
source .env
set +a

stripe listen \
  --api-key "$STRIPE_API_KEY" \
  --events payment_intent.succeeded,payment_intent.payment_failed \
  --forward-to localhost:8082/api/webhooks/stripe
```

En Windows PowerShell:

```powershell
$env:STRIPE_API_KEY = (Get-Content .env | Where-Object { $_ -match '^STRIPE_API_KEY=' }).Split('=', 2)[1]
stripe listen `
  --api-key $env:STRIPE_API_KEY `
  --events payment_intent.succeeded,payment_intent.payment_failed `
  --forward-to localhost:8082/api/webhooks/stripe
```

Stripe CLI mostrará una línea similar a:

```text
Your webhook signing secret is whsec_...
```

### Dónde pegar `whsec_...`

Copia el valor completo que imprime Stripe CLI y pégalo en la variable
`STRIPE_WEBHOOK_SECRET` de [`.env`](./.env):

```env
STRIPE_WEBHOOK_SECRET=whsec_el_valor_mostrado_por_stripe_cli
```

Después de cambiarlo, reconstruye Orders para que tome el nuevo secreto:

```bash
docker compose up -d --build orders
```

Mantén la terminal con `stripe listen` abierta mientras pruebas pagos. Si
detienes y vuelves a iniciar el listener, verifica si Stripe generó un secreto
nuevo y actualiza `.env` si es necesario.

El endpoint correcto es:

```text
http://localhost:8082/api/webhooks/stripe
```

No uses `/api/orders/webhooks/stripe`; esa ruta devuelve `404`.

## Probar el frontend

En otra terminal:

```bash
cd web
npm install
npm start
```

Abre <http://localhost:4200>, registra o inicia sesión, crea una orden y
realiza el pago con una tarjeta de prueba de Stripe, por ejemplo:

```text
4242 4242 4242 4242
```

Usa cualquier fecha futura, cualquier CVC de tres dígitos y un código postal
válido.

El flujo esperado es:

1. La orden se crea como `PENDING`.
2. Orders crea un PaymentIntent.
3. Stripe confirma el pago.
4. Stripe CLI reenvía `payment_intent.succeeded`.
5. Orders cambia la orden a `PAID`.
6. El frontend vuelve a “Mis pedidos” y muestra el mensaje de éxito.
7. Notifications procesa el evento y envía el correo al usuario.

Para revisar logs:

```bash
docker compose logs -f orders
docker compose logs -f notifications
```

## Pruebas y compilación

Frontend:

```bash
cd web
npm test -- --watch=false
npm run build
```

Orders:

```bash
cd services/orders
mvn test
```

Notifications:

```bash
cd services/notifications
mvn test
```

## Notas de seguridad

- No subas `.env` al repositorio.
- No publiques `STRIPE_API_KEY`, `STRIPE_WEBHOOK_SECRET`, credenciales AWS ni
  contraseñas SMTP.
- Las claves mostradas en esta guía son ejemplos.
- Usa Stripe en modo prueba (`sk_test_...`) durante el desarrollo local.
