# Gateway Service

Entrada HTTP de Nexo en `http://127.0.0.1:8082`. Conserva las rutas de cada
servicio después de quitar `/api`:

| Entrada | Destino registrado en Eureka |
| --- | --- |
| `/api/auth/**` | `AUTH-SERVICE` (`/auth/**`) |
| `/api/orders/**` | `ORDERS-SERVICE` (`/orders/**`) |
| `/api/catalog/**` | `CATALOG-SERVICE` (`/catalog/**`) |
| `/api/webhooks/stripe` | `ORDERS-SERVICE` (`/webhooks/stripe`) |

Inicia primero Discovery, luego Auth, Orders y Catalog, y finalmente Gateway con
`mvn spring-boot:run` en cada carpeta. Gateway, Auth y Orders usan `EUREKA_URL`;
Catalog usa `EUREKA_SERVER_URL` para cambiar la dirección del registro.
Angular llama al Gateway directamente en desarrollo y usa `/api` como ruta
relativa en producción. Allí, configura el servidor web para reenviar
`/api/**` al Gateway.

El Gateway enruta; Auth sigue validando sus tokens. La versión actual de Orders
en la rama principal no verifica la identidad del usuario, por lo que sus
endpoints necesitan autorización antes de exponer el Gateway fuera de localhost.
Los límites de CORS incluidos sirven para el desarrollo local.

Para probar únicamente el enrutamiento, `mvn verify` ejecuta pruebas con un
backend HTTP temporal. En esas pruebas no se necesita Eureka ni PostgreSQL.
