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
├── EmployeeDirectoryApplication.java                    # Main Spring Boot Application
│
└── enterprise/                                          # Enterprise DDD + Hexagonal Layer
    │
    ├── domain/                                          # 🎯 DDD Domain Layer
    │   ├── employee/                                    # Employee Bounded Context
    │   │   ├── aggregate/
    │   │   │   └── EmployeeAggregate.java
    │   │   ├── event/
    │   │   │   ├── EmployeeCreatedEvent.java
    │   │   │   ├── EmployeeDeletedEvent.java
    │   │   │   └── EmployeeUpdatedEvent.java
    │   │   ├── exception/
    │   │   │   ├── DuplicateEmployeeEmailException.java
    │   │   │   ├── EmployeeNotFoundException.java
    │   │   │   └── InvalidEmployeeStateException.java
    │   │   ├── factory/
    │   │   │   └── EmployeeFactory.java
    │   │   ├── repository/
    │   │   │   └── EmployeeRepositoryPort.java         # 🔌 Output Port
    │   │   ├── specification/
    │   │   │   └── EmployeeSpecification.java
    │   │   └── valueobject/
    │   │       ├── EmployeeEmail.java
    │   │       ├── EmployeeId.java
    │   │       ├── EmployeeName.java
    │   │       ├── EmployeeStatus.java
    │   │       └── Salary.java
    │   │
    │   ├── department/                                  # Department Bounded Context
    │   │   ├── aggregate/
    │   │   │   └── DepartmentAggregate.java
    │   │   ├── event/
    │   │   │   ├── DepartmentCreatedEvent.java
    │   │   │   ├── DepartmentDeletedEvent.java
    │   │   │   └── DepartmentUpdatedEvent.java
    │   │   ├── exception/
    │   │   │   ├── DepartmentNotFoundException.java
    │   │   │   └── DuplicateDepartmentNameException.java
    │   │   ├── factory/
    │   │   │   └── DepartmentFactory.java
    │   │   ├── repository/
    │   │   │   └── DepartmentRepositoryPort.java       # 🔌 Output Port
    │   │   └── valueobject/
    │   │       ├── DepartmentId.java
    │   │       └── DepartmentName.java
    │   │
    │   └── shared/                                      # Shared Domain Concepts
    │       ├── event/
    │       │   └── DomainEvent.java
    │       ├── exception/
    │       │   └── DomainException.java
    │       └── valueobject/
    │           └── AuditInfo.java
    │
    ├── application/                                     # 📋 Application Service Layer (CQRS)
    │   ├── employee/
    │   │   ├── command/                                 # Write Operations
    │   │   │   ├── CreateEmployeeCommand.java
    │   │   │   ├── UpdateEmployeeCommand.java
    │   │   │   └── DeleteEmployeeCommand.java
    │   │   ├── query/                                   # Read Operations
    │   │   │   ├── GetEmployeeQuery.java
    │   │   │   ├── ListEmployeesQuery.java
    │   │   │   └── SearchEmployeesQuery.java
    │   │   ├── handler/                                 # Use Case Handlers
    │   │   │   ├── CreateEmployeeUseCase.java
    │   │   │   ├── UpdateEmployeeUseCase.java
    │   │   │   ├── DeleteEmployeeUseCase.java
    │   │   │   ├── GetEmployeeUseCase.java
    │   │   │   └── ListEmployeesUseCase.java
    │   │   └── service/
    │   │       └── EmployeeApplicationService.java
    │   │
    │   ├── department/
    │   │   ├── command/                                 # Write Operations
    │   │   │   ├── CreateDepartmentCommand.java
    │   │   │   ├── UpdateDepartmentCommand.java
    │   │   │   └── DeleteDepartmentCommand.java
    │   │   ├── query/                                   # Read Operations
    │   │   │   ├── GetDepartmentQuery.java
    │   │   │   └── ListDepartmentsQuery.java
    │   │   ├── handler/                                 # Use Case Handlers
    │   │   │   ├── CreateDepartmentUseCase.java
    │   │   │   ├── UpdateDepartmentUseCase.java
    │   │   │   ├── DeleteDepartmentUseCase.java
    │   │   │   ├── GetDepartmentUseCase.java
    │   │   │   └── ListDepartmentsUseCase.java
    │   │   └── service/
    │   │       └── DepartmentApplicationService.java
    │   │
    │   └── shared/                                      # Shared Application Ports
    │       ├── port/                                    # 🔌 Output Ports (Interfaces)
    │       │   ├── DomainEventPublisherPort.java
    │       │   └── AuditLogPort.java
    │       └── response/
    │           └── ApiResponse.java
    │
    ├── infrastructure/                                  # 🔌 Hexagonal Adapter Layer
    │   ├── persistence/                                 # Output Adapter: Database (JPA)
    │   │   ├── employee/
    │   │   │   ├── entity/
    │   │   │   │   └── EmployeeJpaEntity.java
    │   │   │   ├── mapper/
    │   │   │   │   └── EmployeePersistenceMapper.java   # Domain ↔ JPA mapping
    │   │   │   └── repository/
    │   │   │       ├── EmployeeJpaRepository.java       # Spring Data interface
    │   │   │       └── EmployeeRepositoryAdapter.java   # Adapter implementing port
    │   │   │
    │   │   └── department/
    │   │       ├── entity/
    │   │       │   └── DepartmentJpaEntity.java
    │   │       ├── mapper/
    │   │       │   └── DepartmentPersistenceMapper.java # Domain ↔ JPA mapping
    │   │       └── repository/
    │   │           ├── DepartmentJpaRepository.java     # Spring Data interface
    │   │           └── DepartmentRepositoryAdapter.java # Adapter implementing port
    │   │
    │   ├── web/                                         # Input Adapter: REST API
    │   │   ├── employee/
    │   │   │   ├── controller/
    │   │   │   │   └── EnterpriseEmployeeController.java
    │   │   │   ├── dto/
    │   │   │   │   ├── CreateEmployeeRequest.java
    │   │   │   │   ├── EmployeeResponse.java
    │   │   │   │   └── UpdateEmployeeRequest.java
    │   │   │   └── mapper/
    │   │   │       └── EmployeeWebMapper.java           # Request/Response ↔ Domain
    │   │   │
    │   │   ├── department/
    │   │   │   ├── controller/
    │   │   │   │   └── EnterpriseDepartmentController.java
    │   │   │   ├── dto/
    │   │   │   │   ├── CreateDepartmentRequest.java
    │   │   │   │   ├── DepartmentResponse.java
    │   │   │   │   └── UpdateDepartmentRequest.java
    │   │   │   └── mapper/
    │   │   │       └── DepartmentWebMapper.java         # Request/Response ↔ Domain
    │   │   │
    │   │   └── shared/
    │   │       └── EnterpriseGlobalExceptionHandler.java
    │   │
    │   ├── messaging/                                   # Output Adapter: Event Messaging (Kafka)
    │   │   ├── consumer/
    │   │   │   ├── AuditEventConsumer.java
    │   │   │   ├── DepartmentEventConsumer.java
    │   │   │   └── EmployeeEventConsumer.java
    │   │   └── dto/
    │   │       ├── AuditEventMessage.java
    │   │       ├── DepartmentEventMessage.java
    │   │       └── EmployeeEventMessage.java
    │   │
    │   ├── event/                                       # Event Infrastructure
    │   │   ├── kafka/
    │   │   │   └── KafkaDomainEventPublisher.java       # Adapter implementing port
    │   │   └── audit/
    │   │       └── Slf4jAuditLogAdapter.java           # Audit logging adapter
    │   │
    │   └── adapter/                                     # Additional Adapters
    │       └── out/
    │           └── messaging/
    │               └── KafkaEventPublishingAdapter.java
    │
    ├── configuration/                                   # ⚙️ Spring Configuration
    │   ├── EnterpriseJpaAuditingConfig.java            # JPA auditing config
    │   ├── EnterpriseOpenApiConfig.java                # Swagger/OpenAPI config
    │   ├── KafkaConsumerConfig.java                    # Kafka consumer config
    │   ├── KafkaProducerConfig.java                    # Kafka producer config
    │   └── KafkaTopicConfig.java                       # Kafka topic creation config
    │
    └── shared/                                          # 🛠️ Shared Utilities & Annotations
        ├── annotation/                                  # (Empty - for custom annotations)
        ├── constant/                                    # (Empty - for shared constants)
        └── util/                                        # (Empty - for utility classes)
```

---

## **Layer Breakdown:**

| Layer | Location | Purpose | Dependencies |
|-------|----------|---------|---|
| **Domain** | `enterprise/domain/` | Pure business logic, aggregates, value objects, ports | None (no dependencies) |
| **Application** | `enterprise/application/` | Use cases, CQRS, orchestration | Depends on Domain |
| **Infrastructure** | `enterprise/infrastructure/` | Adapters, JPA, REST, Kafka | Depends on Domain & Application |
| **Configuration** | `enterprise/configuration/` | Spring beans, external services | Depends on all layers |
| **Shared** | `enterprise/shared/` | Utilities, annotations, constants | Can be used by all |

---

## **Key Architecture Patterns:**

✅ **DDD** - Bounded contexts (employee, department) with aggregates & value objects  
✅ **Hexagonal** - Ports (interfaces) & Adapters (implementations) separating concerns  
✅ **CQRS** - Commands (write), Queries (read) separated  
✅ **Clean Architecture** - Dependency rules: Domain → Application → Infrastructure  
✅ **Event-Driven** - Domain events published via Kafka

## Technology Stack

```text
Java 21
Spring Boot 3
Spring Data JPA
PostgreSQL
Apache Kafka
Swagger/OpenAPI
Maven

DDD
Hexagonal Architecture
CQRS
Domain Events
Event Driven Architecture
```

## Important Notes

- Docker and Docker Compose must be completely removed
- Kafka must remain fully functional and run locally
- PostgreSQL must run locally
- Application must start using: `mvn clean install` && `mvn spring-boot:run`
- Kafka must run locally using an installed Kafka broker
- All existing APIs, business rules, and domain logic remain unchanged
