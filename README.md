# AI Agents for Java/Quarkus Development

This repository demonstrates how AI agents can effectively scaffold and develop Quarkus microservices by following explicit architectural guidelines defined in `AGENTS.md`.

## What's Inside

- **AGENTS.md**: Comprehensive documentation that guides AI agents when working with Quarkus projects
  - Tech stack context (Java 25, Quarkus 3.37.1, Maven)
  - Deterministic scaffolding lifecycle
  - Architectural boundaries and coding standards
  - Operational commands for development

- **billing-service**: A complete billing microservice example built following AGENTS.md guidelines
  - Invoice management with state transitions (DRAFT → ISSUED → PAID → OVERDUE)
  - RESTful API with full CRUD operations
  - Hibernate ORM with Panache (Active Record pattern)
  - PostgreSQL database (auto-configured via Dev Services)
  - Comprehensive test suite

## Prerequisites

- Java 25
- Maven 3.9+ (or use the included `mvnw` wrapper)
- Docker or Podman (for Dev Services to run PostgreSQL)

## Quick Start

### 1. Navigate to the billing service
```bash
cd billing-service
```

### 2. Run in development mode
```bash
./mvnw quarkus:dev
```

This starts Quarkus in dev mode with:
- Live reload enabled
- PostgreSQL automatically started via Testcontainers (Dev Services)
- Dev UI available at http://localhost:8080/q/dev

### 3. Access the application
- **Base URL**: http://localhost:8080
- **API Base**: http://localhost:8080/billing/invoices
- **Dev UI**: http://localhost:8080/q/dev

## API Endpoints

### Invoice Operations

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/billing/invoices` | List all invoices |
| `GET` | `/billing/invoices/{id}` | Get invoice by ID |
| `GET` | `/billing/invoices/customer/{customerId}` | Get invoices for a customer |
| `GET` | `/billing/invoices/overdue` | Get all overdue invoices |
| `POST` | `/billing/invoices` | Create a new invoice |
| `PUT` | `/billing/invoices/{id}` | Update an invoice |
| `PUT` | `/billing/invoices/{id}/issue` | Issue an invoice (DRAFT → ISSUED) |
| `PUT` | `/billing/invoices/{id}/pay` | Mark invoice as paid (ISSUED → PAID) |
| `DELETE` | `/billing/invoices/{id}` | Delete an invoice |

## Example Usage

### Create an Invoice

```bash
curl -X POST http://localhost:8080/billing/invoices \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "CUST-001",
    "dueDate": "2026-08-01T00:00:00",
    "items": [
      {
        "description": "Software License",
        "quantity": 10,
        "unitPrice": 99.99
      },
      {
        "description": "Support Package",
        "quantity": 1,
        "unitPrice": 499.00
      }
    ]
  }'
```

### List All Invoices

```bash
curl http://localhost:8080/billing/invoices
```

### Get Invoice by ID

```bash
curl http://localhost:8080/billing/invoices/1
```

### Issue an Invoice

```bash
curl -X PUT http://localhost:8080/billing/invoices/1/issue
```

### Mark Invoice as Paid

```bash
curl -X PUT http://localhost:8080/billing/invoices/1/pay
```

### Get Customer Invoices

```bash
curl http://localhost:8080/billing/invoices/customer/CUST-001
```

### Get Overdue Invoices

```bash
curl http://localhost:8080/billing/invoices/overdue
```

## Running Tests

### Execute all tests
```bash
./mvnw test
```

### Continuous testing in dev mode
```bash
./mvnw quarkus:dev
# Then press 'r' in the terminal to toggle continuous testing
```

## Building for Production

### Package the application
```bash
./mvnw package
```

### Run the packaged application
```bash
java -jar target/quarkus-app/quarkus-run.jar
```

### Build native executable (requires GraalVM)
```bash
./mvnw package -Dnative
```

## Domain Model

### Invoice
- **id**: Auto-generated Long (Panache entity)
- **customerId**: String - Customer identifier
- **status**: Enum (DRAFT, ISSUED, PAID, OVERDUE, CANCELLED)
- **issueDate**: LocalDateTime - When invoice was issued
- **dueDate**: LocalDateTime - Payment deadline
- **paidDate**: LocalDateTime - When payment was received
- **totalAmount**: BigDecimal - Total invoice amount (auto-calculated)
- **items**: List<InvoiceItem> - Line items

### InvoiceItem
- **id**: Auto-generated Long
- **description**: String - Item description
- **quantity**: Integer - Quantity ordered
- **unitPrice**: BigDecimal - Price per unit
- **totalPrice**: BigDecimal - Total line item price (auto-calculated)

## Key Features Demonstrated

### Quarkus Dev Services
- **Zero-config PostgreSQL**: Automatically starts a PostgreSQL container when running in dev or test mode
- **No manual database setup required**
- **Automatic cleanup** when application stops

### Panache Active Record Pattern
```java
@Entity
public class Invoice extends PanacheEntity {
    public String customerId;
    
    public static List<Invoice> findByCustomerId(String customerId) {
        return list("customerId", customerId);
    }
}
```

### Transaction Management
```java
@PUT
@Path("/{id}/pay")
@Transactional  // Explicit transaction boundary
public Response markAsPaid(@PathParam("id") Long id) {
    Invoice invoice = Invoice.findById(id);
    invoice.status = InvoiceStatus.PAID;
    invoice.paidDate = LocalDateTime.now();
    return Response.ok(invoice).build();
}
```

### Integration Testing with Testcontainers
```java
@QuarkusTest
class BillingResourceTest {
    @Test
    void testCreateInvoice() {
        given()
            .contentType(ContentType.JSON)
            .body(invoice)
            .when().post("/billing/invoices")
            .then()
                .statusCode(201);
    }
}
```

## AI Agent Guidelines

This project follows the patterns defined in **AGENTS.md**, which provides:

1. **Deterministic Scaffolding**: Use official Quarkus tooling instead of hand-crafting config
2. **Architectural Boundaries**: Clear rules for reactive vs. blocking code
3. **Data Access Patterns**: Panache Active Record with explicit transaction management
4. **Validation Strategy**: Immediate test execution after scaffolding

When using AI agents to extend this project, reference `AGENTS.md` for:
- Adding new entities and endpoints
- Implementing business logic
- Testing strategies
- Production packaging

## Project Structure

```
.
├── AGENTS.md                          # AI agent guidelines
├── README.md                          # This file
└── billing-service/                   # Quarkus microservice
    ├── pom.xml                        # Maven dependencies
    ├── src/
    │   ├── main/
    │   │   ├── java/
    │   │   │   └── com/example/billing/
    │   │   │       ├── BillingResource.java    # REST endpoints
    │   │   │       ├── Invoice.java            # Domain entity
    │   │   │       └── InvoiceItem.java        # Line item entity
    │   │   └── resources/
    │   │       └── application.properties      # Configuration (empty - uses defaults)
    │   └── test/
    │       └── java/
    │           └── com/example/billing/
    │               └── BillingResourceTest.java # Integration tests
    └── mvnw                           # Maven wrapper
```

## Tech Stack

- **Framework**: Quarkus 3.37.1 (Supersonic Subatomic Java)
- **Language**: Java 25
- **Build Tool**: Maven with wrapper
- **ORM**: Hibernate ORM with Panache
- **Database**: PostgreSQL (managed by Dev Services)
- **REST**: RESTEasy Reactive with Jackson
- **Testing**: JUnit 5, RestAssured, Testcontainers

## Learn More

- [Quarkus Documentation](https://quarkus.io/guides/)
- [Hibernate ORM with Panache](https://quarkus.io/guides/hibernate-orm-panache)
- [Quarkus Dev Services](https://quarkus.io/guides/dev-services)
- [Building Native Executables](https://quarkus.io/guides/building-native-image)

## License

This is a demonstration project for AI-driven Quarkus development.
