# ms-rutaexpress-catalog

Servicios de envío, tarifas y capacidad de flota.

Spring Boot 3.3.5, Java 17+, Maven (`./mvnw`). Puerto local: **8082**. Responsable: compañero / opencode.

## Endpoints

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/catalog/services` | lista servicios |
| GET | `/api/catalog/services/{id}` | detalle |
| POST | `/api/catalog/services` | crea servicio |
| GET | `/api/catalog/fleet` | flota |
| POST | `/api/catalog/fleet` | crea flota |
| PATCH | `/api/catalog/fleet/{id}/status` | cambia estado de flota |

## Perfiles

- **por defecto (dev)**: H2 en memoria y **sin seguridad** (solo para desarrollo local).
- **`secure`**: valida el JWT de Azure AD (`AZURE_TENANT_ID`) y aplica roles desde el claim `roles`.
- **`prod`**: PostgreSQL.

## Variables de entorno

`AZURE_TENANT_ID`, `AZURE_CLIENT_ID`, DB_HOST/PORT/NAME/USER/PASS (perfil prod)

## Pruebas

`./mvnw test` ejecuta 17 pruebas: servicio y flota (dominio, API con H2 y seguridad por perfil `secure`). No necesitan brokers ni base de datos externos (H2 en memoria; los listeners de RabbitMQ/Kafka se desactivan en los tests).

## Ejecutar

```bash
./mvnw test
./mvnw spring-boot:run
SPRING_PROFILES_ACTIVE=secure AZURE_TENANT_ID=<tenant> ./mvnw spring-boot:run
```

Los DTOs compartidos están copiados en `src/main/java/com/rutaexpress/contracts`; la fuente de verdad de los contratos está en el repo `Cloud-Native-1` (`contratos/`).
