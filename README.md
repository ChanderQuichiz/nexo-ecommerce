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

El backend usa los valores locales definidos directamente en los
`application.yaml` de cada servicio. No requiere archivos `.env`.

Variables importantes:

```env
AUTH_JWT_SECRET=una-clave-Base64-de-al-menos-32-bytes
AUTH_ADMIN_EMAIL=admin@example.com
AUTH_ADMIN_PASSWORD=una-clave-segura
ORDERS_STRIPE_API_KEY=sk_test_...
ORDERS_STRIPE_WEBHOOK_SECRET=whsec_...
AUTH_CORS_ALLOWED_ORIGINS=https://tu-frontend.example.com
GATEWAY_CORS_ALLOWED_ORIGINS=https://tu-frontend.example.com
```

En producción, reemplaza los valores de configuración en los `application.yaml`
por valores gestionados de forma segura. No uses los valores de desarrollo.
`AUTH_ADMIN_NAME`, `AUTH_ADMIN_EMAIL` y
`AUTH_ADMIN_PASSWORD` solo se utilizan para crear o actualizar el usuario
administrador inicial. Cambiar esas variables no cambia las credenciales de
usuarios ya existentes.

`allowed-origins` de Auth y Gateway son listas separadas por comas y
deben contener únicamente los orígenes reales del frontend, incluyendo
protocolo y puerto cuando corresponda; no agregues `/` al final.

`currency` es configuración de negocio y afecta los pagos y los comprobantes.
Actualmente
Orders usa envío gratuito como una constante del dominio y no existe todavía
un flujo de intereses; por eso no se configuran `SHIPPING_FEE` ni
`INTEREST_RATE`.

El frontend se publica como archivos estáticos en S3 detrás de CloudFront.
Angular no puede leer variables de ECS ni `.env` después de compilarse, por lo
que debes generar su configuración antes del build:

```bash
cd web
npm run build:production
```

El comando carga automáticamente [`web/.env.production`](./web/.env.production).
Para desarrollo local usa:

```bash
cd web
npm run build:local
```

que carga [`web/.env`](./web/.env). Estos archivos solo contienen configuración
pública del frontend y están ignorados por Git. En CI/CD, configura sus valores
como variables del pipeline.

El comando genera `public/runtime-config.js` y después crea `dist/web`, que es
la carpeta que debes subir a S3. Para desarrollo, `npm run build` usa `/api`
como URL predeterminada y no requiere una clave Stripe. Nunca coloques
`ORDERS_STRIPE_API_KEY`, `ORDERS_STRIPE_WEBHOOK_SECRET`, `AUTH_JWT_SECRET`, credenciales AWS,
contraseñas de base de datos o SMTP en el código del frontend.

Configura en CloudFront una respuesta de error `403` y otra `404` que devuelvan
`/index.html` con estado `200`, para que funcionen las rutas de Angular.

Para Gmail SMTP:

```env
NOTIFICATIONS_MAIL_HOST=smtp.gmail.com
NOTIFICATIONS_MAIL_PORT=587
NOTIFICATIONS_MAIL_USERNAME=tu-cuenta@gmail.com
NOTIFICATIONS_MAIL_PASSWORD=tu-contraseña-de-aplicacion
NOTIFICATIONS_MAIL_SMTP_AUTH=true
NOTIFICATIONS_MAIL_STARTTLS_ENABLED=true
NOTIFICATIONS_MAIL_FROM=tu-cuenta@gmail.com
```

`NOTIFICATIONS_MAIL_USERNAME` y `NOTIFICATIONS_MAIL_PASSWORD` pertenecen a la cuenta que envía los
correos. El destinatario se obtiene del usuario de la orden y no se coloca en
un archivo de entorno. La contraseña debe ser una App Password de Google.

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
misma cuenta de Stripe cuya clave está configurada en el `application.yaml` de
Orders.

### Windows

Instala Stripe CLI desde la documentación oficial:
<https://docs.stripe.com/stripe-cli>

Después ejecuta:

```powershell
stripe login
```

## Configurar el webhook local

Ejecuta Stripe CLI desde la raíz del proyecto:

```bash
stripe listen \
  --api-key "sk_test_replace-me" \
  --events payment_intent.succeeded,payment_intent.payment_failed \
  --forward-to localhost:8082/api/webhooks/stripe
```

En Windows PowerShell:

```powershell
stripe listen `
  --api-key sk_test_replace-me `
  --events payment_intent.succeeded,payment_intent.payment_failed `
  --forward-to localhost:8082/api/webhooks/stripe
```

Stripe CLI mostrará una línea similar a:

```text
Your webhook signing secret is whsec_...
```

### Dónde pegar `whsec_...`

Copia el valor completo que imprime Stripe CLI y pégalo en la propiedad
`stripe.webhook.secret` del `application.yaml` de Orders:

```env
secret: whsec_el_valor_mostrado_por_stripe_cli
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
- No publiques `ORDERS_STRIPE_API_KEY`, `ORDERS_STRIPE_WEBHOOK_SECRET`, credenciales AWS ni
  contraseñas SMTP.
- Las claves mostradas en esta guía son ejemplos.
- Usa Stripe en modo prueba (`sk_test_...`) durante el desarrollo local.
