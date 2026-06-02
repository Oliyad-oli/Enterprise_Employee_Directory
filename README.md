# Employee Directory Enterprise - DDD + Hexagonal Architecture

## Overview

This project contains **two implementations** side by side:

| Implementation | Base Package | API Prefix | Purpose |
|---|---|---|---|
| Original (legacy) | `...employeedirectory` | `/employees`, `/departments` | Functional baseline |
| Enterprise (new) | `...employeedirectory.enterprise` | `/api/v2/enterprise/...` | DDD + Hexagonal showcase |

## Enterprise Architecture

The enterprise layer implements:
- **Domain-Driven Design** (Aggregates, Value Objects, Domain Events, Factories)
- **Hexagonal Architecture** (Ports & Adapters)
- **CQRS** (Commands, Queries, Handlers)
- **Clean Architecture** (dependency rules)
- **Event-Driven** (domain events published via Kafka-ready publisher)

## Quick Start

```bash
# Prerequisites: PostgreSQL running on localhost:5432
# Database: employee_directory
# User: intern_user / intern123

mvn clean install
mvn spring-boot:run
```

## API Endpoints

### Enterprise API (new)
- `POST   /api/v2/enterprise/departments`
- `GET    /api/v2/enterprise/departments`
- `GET    /api/v2/enterprise/departments/{id}`
- `PUT    /api/v2/enterprise/departments/{id}`
- `DELETE /api/v2/enterprise/departments/{id}`
- `POST   /api/v2/enterprise/employees`
- `GET    /api/v2/enterprise/employees`
- `GET    /api/v2/enterprise/employees/{id}`
- `PUT    /api/v2/enterprise/employees/{id}`
- `DELETE /api/v2/enterprise/employees/{id}`
- `GET    /api/v2/enterprise/employees/search?keyword=`
- `GET    /api/v2/enterprise/employees/filter?minSalary=&maxSalary=`

### Swagger UI
`http://localhost:8080/swagger-ui.html`

### Original API (unchanged)
- `/employees`, `/departments` — fully preserved

## Project Structure

```
src/main/java/com/act/intern/employeedirectory/
├── controller/         # Original controllers (untouched)
├── service/            # Original services (untouched)
├── repository/         # Original repositories (untouched)
├── domain/             # Original JPA entities (untouched)
└── enterprise/
    ├── domain/
    │   ├── employee/   # Employee bounded context
    │   └── department/ # Department bounded context
    ├── application/    # Use cases, commands, queries, handlers
    ├── infrastructure/ # JPA adapters, REST controllers, Kafka publisher
    ├── shared/         # Utilities
    └── configuration/  # Spring configuration
```
