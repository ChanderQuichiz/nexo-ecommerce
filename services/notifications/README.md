# Notifications Service

Microservicio encargado de notificar al cliente los cambios relevantes de sus órdenes y de generar comprobantes PDF. Consume eventos de Orders, consulta los datos necesarios en Orders y Auth, envía correos y ofrece un endpoint HTTP para descargar comprobantes.

## Arquitectura

El proyecto sigue arquitectura hexagonal con conceptos tácticos de DDD:

- `domain`: entidades, value objects, excepciones y reglas de negocio; no depende de Spring.
- `application`: casos de uso, DTO internos y puertos requeridos por el negocio.
- `infrastructure`: adaptadores Feign, RabbitMQ, Spring Mail y JasperReports, además de configuración de Spring.
- `presentation`: adaptadores HTTP y manejo uniforme de errores de la API.

## Tecnologías

Java 21, Spring Boot, Spring AMQP, RabbitMQ, OpenFeign, Eureka Client, Spring Mail, JasperReports, Actuator, JUnit 5, Mockito y Testcontainers.

## Eventos consumidos

Notifications escucha `notifications.queue`, enlazada a `orders.exchange`:

| Routing key | Evento | Acción |
| --- | --- | --- |
| `orders.created` | `OrderCreatedEvent` | Envía confirmación de creación. |
| `orders.updated` | `OrderUpdateEvent` | Notifica estados `SHIPPED`, `DELIVERED` y `CANCELED`. |
| `orders.paid` | `OrderPaidEvent` | Genera y adjunta el comprobante PDF. |

El listener realiza como máximo tres intentos por defecto y no reencola indefinidamente los mensajes rechazados.

## Endpoint HTTP

`GET /notifications/generate-receipt/{orderId}` genera y descarga `receipt-{orderId}.pdf`. Solo admite órdenes en estado `PAID`, `SHIPPED` o `DELIVERED`. El contrato completo está en `docs/notifications-service.yaml`.

El comprobante se genera completamente en memoria; Notifications no utiliza base de datos ni almacena archivos PDF.

## Variables de entorno

Consulta el archivo local `.env` para la lista completa. Las variables principales son:

- `SERVER_PORT`
- `RABBITMQ_HOST`, `RABBITMQ_PORT`, `RABBITMQ_USERNAME`, `RABBITMQ_PASSWORD`
- `RABBITMQ_RETRY_MAX_ATTEMPTS`, `RABBITMQ_RETRY_INITIAL_INTERVAL`, `RABBITMQ_RETRY_MULTIPLIER`, `RABBITMQ_RETRY_MAX_INTERVAL`
- `ORDERS_SERVICE_URL`, `AUTH_SERVICE_URL`, `INTERNAL_API_KEY`
- `EUREKA_SERVER_URL`, `EUREKA_CLIENT_ENABLED`
- `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD`, `MAIL_FROM`
- `RECEIPT_CURRENCY`

El archivo `.env` está excluido de Git y puede contener las credenciales locales. No lo incorpores al repositorio.

## Desarrollo local

Levanta únicamente RabbitMQ:

```powershell
docker compose up -d
```

La consola de administración queda disponible en <http://localhost:15672>. El microservicio se ejecuta desde IntelliJ o con:

```powershell
.\mvnw.cmd spring-boot:run
```

Para Mailtrap, crea manualmente un inbox SMTP, copia host, puerto, usuario y contraseña a variables de entorno locales y no confirmes esas credenciales en Git. El adaptador no prueba la conexión SMTP al iniciar.

## Pruebas

```powershell
.\mvnw.cmd clean test
.\mvnw.cmd clean verify
```

`clean verify` incluye la integración RabbitMQ con Testcontainers. Esa prueba se omite automáticamente cuando Docker no está disponible y nunca consulta Auth, Orders o Catalog ni envía correos o genera PDF.

## Dependencias externas

- Orders Service: publica eventos y expone los datos de la orden.
- Auth Service: expone los datos del cliente. Notifications envía `X-Internal-Api-Key` en esta comunicación interna.
- Eureka: resolución opcional de los servicios cuando no se configuran URLs directas.
- RabbitMQ: transporte de los eventos de Orders.
- SMTP/Mailtrap: entrega de correos.

## Coordinaciones pendientes

- Auth debe validar `X-Internal-Api-Key`.
- Gateway debe validar el JWT antes de exponer el endpoint HTTP.
- La propiedad de `notifications.queue` debe trasladarse a Notifications y eliminarse su declaración en Orders antes de configurar DLX y DLQ; declararla ahora con argumentos distintos causaría `PRECONDITION_FAILED`.
- Transactional Outbox y un `eventId` para idempotencia quedan como mejoras futuras.
- Los productos aparecen mediante `productId` porque Orders no devuelve sus nombres.
