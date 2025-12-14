# OrderManagementSystem (Lean)
A focused Spring Boot 3 application that proves a simple order/contract/customer workflow with a server-rendered UI.

## Overview
- The app exposes **Thymeleaf-based web pages** for customers, orders, and contracts; there are no REST or `/api/...` controllers in the current sources.
- Backing persistence is handled by Spring Data JPA using MySQL / MariaDB by default (`jdbc:mysql://localhost:3306/oms`, credentials `root/admin` inside `application.properties`), but the project will happily run with any JDBC sink as long as the schema can be created.
- Validation annotations (`jakarta.validation`) and `@ControllerAdvice` global exception handling keep user feedback consistent across forms.

## Running the app
1. Ensure Java 17+ and Maven 4.x.x are installed.
2. Update `OrderManagementSystemLean/src/main/resources/application.properties` to point the datasource to a reachable MySQL instance or an in-memory H2 database if desired.
3. Run `mvn -pl OrderManagementSystemLean spring-boot:run` from the repository root (or enter the module and run `mvn spring-boot:run`).
4. On startup `DataInitializer` seeds units, sellable items, contract types, customers, contracts and orders; it also inspects existing contracts/orders and adds lines if they are empty.

```bash
mvn -pl OrderManagementSystemLean spring-boot:run
```

## Data Model
- **Customer** (`com.example.OrderManagementSystem.model.Customer`) stores name, currency, email and a lazy `orders` collection. Orders cascade deletion via `CascadeType.ALL` / `orphanRemoval = true`.
- **Order** keeps the order date, status (`OrderStatus` enum), shipping address, customer and an optional contract. Each order owns multiple `OrderLine` entries that refer to `SellableItem`/`UnitOfMeasure`.
- **Contract** exposes an inner `ContractStatus` enum and associates with a `ContractType`. It owns `ContractLine` entries, mirroring the structure of order lines.
- **SellableItem/Product/Service** is a single-table inheritance model. `SellableItem` stores the shared metadata; `Product` overrides `getUnitValue()` and adds description/value, while `Service` exposes a `SellableItemStatus`.
- **OrderLine/ContractLine** each maintain quantity, item, and unit references plus a transient helper to compute total value (`item.getUnitValue() * quantity`).
- **UnitOfMeasure** stores the human-readable name and symbol used across order and contract lines.

## Data Initialization & Fix-up
- `com.example.OrderManagementSystem.config.DataInitializer` runs on startup (`CommandLineRunner`). It:
  1. Seeds units and sellable items when none exist.
  2. Ensures a minimal set of contract types and customers.
  3. Seeds at least five contracts and orders that each include at least one line.
  4. Visits every existing contract/order on every boot and populates missing lines with random items/units so legacy rows are not left without detail lines.
  5. Normalizes the `order_date` column (using `JdbcTemplate` to clean zero/`NULL` placeholders) before any order seeding runs so the new `orderDate` mapping (`columnDefinition = "DATE NOT NULL DEFAULT CURRENT_DATE"`) can be applied safely under MySQL strict mode.

## Repositories & Services
- Each entity has a Spring Data `JpaRepository` and a matching `@Service` that acts as a thin delegation layer (e.g., `CustomerService` wraps `CustomerRepository`, `OrderService` wraps `OrderRepository`).
- `OrderRepository` extends `JpaSpecificationExecutor` and applies `@EntityGraph(attributePaths = {"customer","contract","orderLines"})` to eager-load the associations used in the UI.
- `OrderSpecifications.withFilters(...)` builds `javax.persistence.criteria` predicates for name, customer name, status, and order date ranges so the UI can filter without manual SQL.
- Additional repositories include `ContractRepository`, `ContractLineRepository`, `ContractTypeRepository`, `SellableItemRepository`, `UnitOfMeasureRepository`, and `OrderLineRepository`.

## Web Layer & Controllers
- `CustomerWebController` manages list/details/new/edit/delete flows for customers with standard validation + redirect-on-save patterns.
- `OrderWebController` handles filtering (`name`, `customerName`, `OrderStatus`, date range plus user-provided `sortBy`/`sortDir`) through `OrderSpecifications`, exposes details/edit forms, enforces a minimum of three blank order-lines during editing, and resolves dropdown selections with custom `PropertyEditorSupport` binders for `SellableItem` and `UnitOfMeasure`.
- `ContractWebController` mirrors the order UI: it enforces a minimum of three lines, resolves contract type/item/unit selections via `PropertyEditorSupport`, and treats updates specially by loading the persistent `Contract`, copying editable fields, clearing previous `ContractLine` entries, then re-attaching the sanitized lines before saving.
- `GlobalExceptionHandler` (`@ControllerAdvice`) redirects uncaught exceptions to `templates/error/general-error.html` while logging the stack trace.

## Thymeleaf Templates & Static Assets
- Templates under `src/main/resources/templates/{customers,orders,contracts}` use inline CSS for a consistent “smart blue” look and reuse the shared navigation bar style defined in `static/css/style.css`.
- Each entity gets an `index.html`, `details.html`, and `form.html`, and the order/contract forms iterate over order/contract lines using `th:each` with `__${iterStat.index}__` notation to bind complex nested lists.
- The order list (`orders/index.html`) preserves filter/sort inputs, renders the status with inline styling, and includes `orders/{id}/edit`, `/new`, and delete forms that leverage Spring MVC CSRF tokens automatically.
- The customers/contracts detail templates show summary tables, while `error/general-error.html` surfaces the message from `GlobalExceptionHandler`.

## Known Issues & Risks
1. There is no REST `/api/**` layer in the current sources—only Thymeleaf `CustomerWebController`, `OrderWebController`, and `ContractWebController`. Any API clients clearing the old documentation will need to be re-implemented with `@RestController` endpoints (or the controllers exposed via a `@Controller`-sourced API).
2. `OrderWebController.listOrders(...)` uses the `sortBy` query parameter directly inside `Sort.by(sortBy)` (`OrderManagementSystemLean/src/main/java/com/example/OrderManagementSystem/web/OrderWebController.java:101`). Without validating `sortBy` against a whitelist of known properties, any arbitrary string will either cause `Sort.by(...)` to blow up (`IllegalArgumentException`) or surface unexpected columns; the parameter should be sanitized before it reaches Spring Data.
