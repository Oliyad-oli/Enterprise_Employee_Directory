# Hexagonal Architecture Guide

## Core Concept

The application is surrounded by ports (interfaces). Adapters plug into these ports.

## Inbound Adapters (Driving)
- `EnterpriseEmployeeController` — HTTP REST
- `EnterpriseDepartmentController` — HTTP REST

## Input Ports (Interfaces)
- `CreateEmployeeUseCase`, `UpdateEmployeeUseCase`, `DeleteEmployeeUseCase`
- `GetEmployeeUseCase`, `ListEmployeesUseCase`
- `CreateDepartmentUseCase`, `UpdateDepartmentUseCase`, `DeleteDepartmentUseCase`
- `GetDepartmentUseCase`, `ListDepartmentsUseCase`

## Application Services (Use Case Implementations)
- `EmployeeApplicationService` — implements all 5 employee use cases
- `DepartmentApplicationService` — implements all 5 department use cases

## Output Ports (Interfaces)
- `EmployeeRepositoryPort` — in domain layer
- `DepartmentRepositoryPort` — in domain layer
- `DomainEventPublisherPort` — in application shared
- `AuditLogPort` — in application shared

## Outbound Adapters (Driven)
- `EmployeeRepositoryAdapter` — JPA implementation
- `DepartmentRepositoryAdapter` — JPA implementation
- `KafkaDomainEventPublisher` — Kafka-ready event publisher
- `Slf4jAuditLogAdapter` — SLF4J audit logging
