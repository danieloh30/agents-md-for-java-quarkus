## Tech Stack & Ecosystem Context
- **Runtime**: Java 25, Quarkus 3.x (Supersonic Subatomic Java).
- **Build Tool**: Maven (`mvnw` wrapper present).
- **Extensions**: REST, Hibernate ORM with Panache, Quarkus Dev Services.
- **Database**: PostgreSQL (Managed entirely via Dev Services).

## Critical Operational Commands
- **Launch Development Mode**: `./mvnw quarkus:dev`
- **Execute All Tests**: `./mvnw test`
- **Continuous Testing**: Start `./mvnw quarkus:dev` and press `r` to toggle background testing.
- **Production Package**: `./mvnw package`

## Architectural Boundaries & Coding Standards

### 1. Reactive vs. Blocking Rules
- Default to **REST**. Endpoints returning `Uni<T>` or `Multi<T>` must NEVER invoke blocking operations.
- If a method blocks, annotate it explicitly with `@Blocking`.

### 2. Data Access (Hibernate ORM with Panache)
- Use the **Panache Active Record pattern** extending `PanacheEntity`. Do NOT write custom repositories or explicit DAO layers unless complex business logic demands it.
- **Transaction Management**: Annotate mutate operations with `@Transactional`. Never manage transactions manually.

```java
// Correct Agent Output Example:
@Entity
public class Developer extends PanacheEntity {
    public String name;
    public String specialty;

    public static Uni<Developer> findByName(String name) {
        return find("name", name).firstResult();
    }
}
```

## Scaffolding Lifecycle for New Microservices

When scaffolding a new microservice (e.g., "Scaffold a new microservice for user billing"), the agent follows this deterministic lifecycle:

### 1. Reads the Command Layer
- **Bypass manual configuration**: Do NOT generate raw `pom.xml` text by hand, which frequently leads to version mismatches or missing dependency management blocks.
- **Use Quarkus tooling**: Rely on the official Quarkus Maven plugin command structure.

### 2. Executes the Tooling
- **Command**: Run the explicit `mvn io.quarkus.platform:quarkus-maven-plugin:create` command directly inside your terminal workspace.
- **Example**:
```bash
mvn io.quarkus.platform:quarkus-maven-plugin:3.x.x:create \
  -DprojectGroupId=com.example \
  -DprojectArtifactId=billing-service \
  -DclassName="com.example.billing.BillingResource" \
  -Dpath="/billing"
```

### 3. Applies Core Extensions
- **Guarantee essential extensions** are baked in from the first second:
  - `hibernate-orm-panache` for data access
  - `rquarkus-rest` for REST endpoints
- **Add extensions during creation**:
```bash
mvn io.quarkus.platform:quarkus-maven-plugin:create \
  ... \
  -Dextensions="hibernate-orm-panache,quarkus-rest,jdbc-postgresql"
```
- This prevents the agent from creating legacy or blocking code templates down the line.

### 4. Validates Context
- **Transition to Testing**: Once scaffolded, immediately verify that the out-of-the-box generated test suite runs cleanly.
- **Validation command**: `./mvnw test`
- **Expected outcome**: All generated tests pass without modification, confirming the scaffold is valid and ready for development.

### Post-Scaffold Checklist
- [ ] Project structure follows standard Maven layout (`src/main/java`, `src/test/java`)
- [ ] `application.properties` contains Dev Services configuration (auto-configured for PostgreSQL)
- [ ] At least one REST endpoint exists with a corresponding test
- [ ] `./mvnw test` passes cleanly
- [ ] `./mvnw quarkus:dev` starts without errors