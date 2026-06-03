# Architecture Overview

## Hexagonal Architecture (Ports & Adapters)

```
┌─────────────────────────────────────────────────────────────┐
│                    INBOUND ADAPTERS                         │
│            REST Controllers (HTTP/JSON)                     │
└───────────────────────────┬─────────────────────────────────┘
                            │  Input Ports (Use Case Interfaces)
┌───────────────────────────▼─────────────────────────────────┐
│                    APPLICATION LAYER                        │
│   EmployeeApplicationService, DepartmentApplicationService  │
│          Commands | Queries | Handlers                      │
└───────────────────────────┬─────────────────────────────────┘
                            │  Output Ports (Repository Interfaces)
┌───────────────────────────▼─────────────────────────────────┐
│                     DOMAIN LAYER                            │
│  Aggregates | Value Objects | Domain Events | Domain Rules  │
└───────────────────────────┬─────────────────────────────────┘
                            │
┌───────────────────────────▼─────────────────────────────────┐
│                   OUTBOUND ADAPTERS                         │
│   JPA Repositories | Kafka Publisher | Audit Log Adapter    │
└─────────────────────────────────────────────────────────────┘
```

## Dependency Rule

```
Infrastructure → Application → Domain
```

Domain has zero dependencies on outer layers.

## Database Tables

| Table | Purpose |
|---|---|
| `departments` | Original departments (V1 migration) |
| `employees` | Original employees (V2 migration) |
| `ent_departments` | Enterprise departments with soft-delete, audit (V3) |
| `ent_employees` | Enterprise employees with soft-delete, status, audit (V4) |
