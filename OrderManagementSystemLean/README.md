# OrderManagementSystem (Lean)

A minimal, **non-bloated** Spring Boot app with in-memory repositories.
- No DTOs, no custom exception layer, no complicated mappers.
- Simple controllers → thin services → in-memory repositories.
- All IDs are `Long`.
- Ready to run with: `mvn spring-boot:run` (Java 17).

## API (examples)
- `GET /api/health`
- `POST /api/customers` with `{ "name":"Acme", "currency":"EUR" }`
- `POST /api/orders` with `{ "name":"Order A" }`
- `POST /api/contracts` with `{ "name":"Main", "status":"Active", "contractTypeId":1 }`
