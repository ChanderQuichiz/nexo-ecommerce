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
```

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
