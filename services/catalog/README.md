# Catalog service

Microservicio de productos, categorías, búsqueda y stock de Nexo Ecommerce.
Utiliza PostgreSQL como base de datos independiente del microservicio Orders.

## Requisitos

- Java 21
- Maven 3.9+
- Docker Desktop

## Etapa 1: ejecutar PostgreSQL y la aplicación

Desde la carpeta `services/catalog`:

```bash
docker compose up -d
mvn spring-boot:run
```

La aplicación se ejecuta en `http://localhost:8081` y su estado se consulta en:

```text
GET http://localhost:8081/actuator/health
```

Para detener PostgreSQL sin borrar sus datos:

```bash
docker compose down
```

PostgreSQL se publica en el puerto `5433` para no entrar en conflicto con
Orders, que utiliza el puerto `5432`. El volumen `catalog_db_data` conserva la
información entre reinicios.

## Etapa 2: modelo de datos

Flyway ejecuta `V1__Init.sql` al iniciar la aplicación por primera vez y crea:

- `categories`: categorías únicas que pueden activarse o desactivarse.
- `products`: productos con precio, stock, imagen, estado y categoría.

Una categoría puede tener muchos productos y cada producto pertenece a una
categoría.

## Etapa 3: API de categorías

| Método | Endpoint | Descripción |
| --- | --- | --- |
| POST | `/catalog/categories` | Crear una categoría |
| GET | `/catalog/categories` | Listar categorías |
| GET | `/catalog/categories/{id}` | Consultar una categoría |
| PUT | `/catalog/categories/{id}` | Actualizar nombre y descripción |
| PATCH | `/catalog/categories/{id}/status` | Activar o desactivar |
