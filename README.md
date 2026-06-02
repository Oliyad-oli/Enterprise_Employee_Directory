# Project Structure

```text
src/main/java/com/act/intern/employeedirectory/
│
├── EmployeeDirectoryApplication.java
│
├── domain/
│   ├── model/
│   │   ├── Employee.java
│   │   ├── Department.java
│   │   └── valueobject/
│   │       ├── EmployeeId.java
│   │       ├── DepartmentId.java
│   │       └── Email.java
│   │
│   ├── event/
│   │   ├── DomainEvent.java
│   │   ├── EmployeeCreatedEvent.java
│   │   ├── EmployeeUpdatedEvent.java
│   │   ├── EmployeeDeletedEvent.java
│   │   ├── DepartmentCreatedEvent.java
│   │   ├── DepartmentUpdatedEvent.java
│   │   └── DepartmentDeletedEvent.java
│   │
│   ├── port/
│   │   ├── in/
│   │   │   ├── CreateEmployeeUseCase.java
│   │   │   ├── UpdateEmployeeUseCase.java
│   │   │   ├── DeleteEmployeeUseCase.java
│   │   │   ├── GetEmployeeUseCase.java
│   │   │   ├── CreateDepartmentUseCase.java
│   │   │   ├── UpdateDepartmentUseCase.java
│   │   │   ├── DeleteDepartmentUseCase.java
│   │   │   └── GetDepartmentUseCase.java
│   │   │
│   │   └── out/
│   │       ├── EmployeeRepositoryPort.java
│   │       ├── DepartmentRepositoryPort.java
│   │       └── DomainEventPublisherPort.java
│   │
│   └── exception/
│       ├── EmployeeNotFoundException.java
│       └── DepartmentNotFoundException.java
│
├── application/
│   ├── service/
│   │   ├── EmployeeApplicationService.java
│   │   └── DepartmentApplicationService.java
│   │
│   ├── command/
│   │   ├── employee/
│   │   └── department/
│   │
│   ├── query/
│   │   ├── employee/
│   │   └── department/
│   │
│   └── mapper/
│
├── infrastructure/
│   ├── adapter/
│   │
│   │   ├── in/
│   │   │   └── web/
│   │   │       ├── EmployeeController.java
│   │   │       └── DepartmentController.java
│   │   │
│   │   └── out/
│   │       ├── persistence/
│   │       │   ├── entity/
│   │       │   ├── repository/
│   │       │   ├── mapper/
│   │       │   ├── EmployeePersistenceAdapter.java
│   │       │   └── DepartmentPersistenceAdapter.java
│   │       │
│   │       └── messaging/
│   │           ├── KafkaDomainEventPublisher.java
│   │           ├── EmployeeEventConsumer.java
│   │           ├── DepartmentEventConsumer.java
│   │           └── AuditEventConsumer.java
│   │
│   ├── configuration/
│   │   ├── KafkaConfig.java
│   │   ├── KafkaTopicConfig.java
│   │   ├── PersistenceConfig.java
│   │   └── OpenApiConfig.java
│   │
│   └── exception/
│       └── GlobalExceptionHandler.java
│
└── shared/
    ├── constants/
    ├── utils/
    └── dto/
```

## Removed Completely

The following must NOT exist anywhere in the project:

```text
Dockerfile

docker-compose.yml

.dockerignore

docker/
deployment/

container/

kubernetes/

helm/
```

## Runtime Architecture

```text
Client
   │
   ▼
REST Controller
   │
   ▼
Application Service
   │
   ▼
Domain Model
   │
   ▼
Ports
   │
   ├── Persistence Adapter
   │        ▼
   │    PostgreSQL
   │
   └── Event Publisher Adapter
            ▼
         Kafka
            ▼
      Kafka Consumers
```

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

## Important

Docker and Docker Compose must be completely removed.

Kafka must remain fully functional.

PostgreSQL must run locally.

Application must start using:

```bash
mvn clean install

mvn spring-boot:run
```

Kafka must run locally using an installed Kafka broker.

All existing APIs, business rules, database schema, domain logic, CQRS flows, domain events, and Hexagonal Architecture layers must remain unchanged.

```
```
