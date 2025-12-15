# E-Commerce Purchase Invoice Service

A Java Spring Boot application for managing purchase invoices, using PostgreSQL and fully containerized with Docker.

This service handles invoice creation, approval, rejection, and cancellation with business rules applied per user credit limits. It also provides notifications for rejected or canceled invoices. The system implements JWT-based security, caching, and standardized JSON responses for consistent API behavior.

---

## Table of Contents

- [Features](#features)
- [Technologies](#technologies)
- [Setup](#setup)
  - [Local Setup](#local-setup)
  - [Docker Setup](#docker-setup)
- [API Endpoints](#api-endpoints)
- [Configuration](#configuration)
- [Security](#security)
- [Exception Handling](#exception-handling)
- [Caching & Performance](#caching--performance)
- [Testing & Edge Cases](#testing--edge-cases)
- [OOP & SOLID Principles](#oop--solid-principles)
- [Docker Structure](#docker-structure)
- [Notes](#notes)

---

## Features

- Create, approve, and cancel purchase invoices. (**No update or delete operations for financial risk mitigation.**)
- Credit limit checks with logging of rejected invoices.
- CRUD operations for products with caching.
- Role-based user management (e.g., `PURCHASING_SPECIALIST`, `FINANCE_SPECIALIST`).
  - Only `PURCHASING_SPECIALIST` users can create invoices for their own records.
  - Only `FINANCE_SPECIALIST` users can view rejected or canceled invoices.
- JWT-based authentication and authorization with database-backed user management.
- Caching with **Caffeine** for improved performance.
- Webhook notifications for rejected and canceled invoices.
- Fully portable via **Docker** and **Docker Compose**.
- API testing with **Swagger UI** and **Postman Collection**.
- Standardized JSON response structure for consistent API feedback.

---

## Technologies

- Java 17
- Spring Boot 3.3.5
- Spring Data JPA / Hibernate
- PostgreSQL 13
- Docker & Docker Compose
- Caffeine Cache
- MapStruct
- JWT
- Maven

---

## Exception Handling

The service follows a robust exception handling strategy:

- **InvoiceExceptions**
  - `InvoiceOwnershipException` → Thrown if a user attempts to manipulate invoices that don’t belong to them.
  - `InvoiceCannotBeCancelledException` → Thrown if a user tries to cancel a non-cancellable invoice (e.g., rejected).
  - `DuplicateBillNoException` → Thrown if a bill number already exists among approved invoices.
- **ProductExceptions**
  - `ProductNotFoundException` → Thrown when the requested product does not exist.
  - `ProductAlreadyExistsException` → Thrown when trying to create a duplicate product.
- **UserExceptions**
  - `UserNotFoundException` / `InvalidCredentialsException` / `EmailAlreadyExistsException` for authentication scenarios.
- All exceptions return **standardized JSON responses** with proper HTTP status codes.

---

## Caching & Performance

- **Products cache:**
  - `products` → List of all products
  - `productsById` → Individual product lookup
- Cache is updated or evicted upon product creation, update, or deletion to maintain consistency.
- Caching improves response times for frequently queried product data.

---

## Testing & Edge Cases

- Unit tests cover **all core business logic**:
  - Invoice approval/rejection depending on user credit limit.
  - Duplicate bill number prevention.
  - Ownership validation.
  - Product existence check.
  - Invoice cancellation rules.
- Edge cases tested:
  - Invoice amount exactly at credit limit.
  - Multiple invoices approaching limit.
  - Identity mismatch.
  - Attempt to cancel rejected invoices or invoices of other users.

---

## OOP & SOLID Principles

- **Single Responsibility Principle (SRP):** Each service handles a single domain:
  - `InvoiceServiceImpl` → invoice lifecycle
  - `ProductServiceImpl` → product management

- **Open/Closed Principle (OCP):** Easily extendable for new invoice rules or notification channels.
- **Liskov Substitution Principle (LSP):** Interfaces (`InvoiceService`, `ProductService`, `AuthService`) allow flexible substitutions.
- **Interface Segregation Principle (ISP):** Service interfaces expose only necessary methods for their clients.
- **Dependency Inversion Principle (DIP):** Services depend on abstractions (repositories, mappers, utilities), not concrete implementations.
- **Best Practices Applied:**
  - Logging with `Slf4j` for audit and debugging.
  - DTO mapping with **MapStruct** to separate persistence models from API layer.
  - Transactional boundaries clearly defined (`@Transactional`).
  - Security and business logic separation.
  - Read-only transactions for query operations to optimize performance.

---

## Setup

### Local Setup

1. Install PostgreSQL and create a database named `emlakjet`.
2. Configure database credentials and JWT secret in `application.yml`.
3. Build the project:

```bash
mvn clean install
```

4. Run the application:

```bash
mvn spring-boot:run
```

---

### Docker Setup

#### 1. Build from project

```bash
docker-compose up --build
```

#### 2. Pull image from Docker Hub

```bash
docker pull halilibrahimozturk/purchase-invoice-service:v1.0.0
docker run -p 8080:8080 halilibrahimozturk/purchase-invoice-service:v1.0.0
```

---

## API Endpoints

- `/api/invoices` → Invoice operations
- `/api/products` → Product CRUD operations
- `/api/notifications` → Notification management
- `/api/auth/**` → Authentication & JWT
- `/mock-webhook` → Mock webhook listener

- Swagger UI: `/swagger-ui/index.html`
- Postman Collection exists in project repo: `/docs/postman_collection.json`

---

## Configuration

- Credit limits, JWT, and database connection are configurable in `application.yml`.

---

## Security

- JWT-based authentication and authorization
- Role-based endpoint access
- Passwords hashed with **BCrypt**

---

## Docker Structure

- Refer to Dockerfile and docker-compose.yml in the project root.

---

## Notes

- Seamless local/Docker switching with environment variables.
- Webhooks simulated via mock listener with database persistence.

