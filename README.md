
# Reactive Product Hub

API reactiva para la gestión e integración de productos.

## Tecnologías

- Java 17
- Spring Boot 4.0.8
- Spring WebFlux y Project Reactor
- Spring Data R2DBC
- PostgreSQL 17
- Flyway
- WebClient
- OpenAPI / Swagger
- Docker y Docker Compose

## Requisitos

- Docker Desktop
- Docker Compose
- Archivo `.env` configurado a partir de `.env.example`

## Ejecutar con Docker

Construir las imágenes:

```bash
docker compose build
```

Iniciar la aplicación y PostgreSQL:

```bash
docker compose up -d
```

Consultar los registros:

```bash
docker compose logs -f app
```

Detener los servicios:

```bash
docker compose down
```

## Endpoints

- Productos: `/api/v1/products`
- Productos externos: `/api/v1/external-products/{id}`
- Resumen de producto: `/api/v1/products/{localId}/overview?externalId={externalId}`
- Swagger UI: `/swagger-ui/index.html`
- OpenAPI JSON: `/v3/api-docs`
- Health: `/actuator/health`

La disponibilidad de cada endpoint depende de que la aplicación haya iniciado correctamente.
