# Kafka Architecture — DDD + Hexagonal

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────┐
│                    DOMAIN LAYER                         │
│                                                         │
│  EmployeeAggregate ──► EmployeeCreatedEvent             │
│  DepartmentAggregate ──► DepartmentCreatedEvent         │
│                                                         │
│  DomainEventPublisherPort  ◄── (interface / Port)       │
└─────────────────────┬───────────────────────────────────┘
                      │ implements
┌─────────────────────▼───────────────────────────────────┐
│                 APPLICATION LAYER                        │
│                                                         │
│  EmployeeApplicationService                             │
│    aggregate.create() → events                          │
│    eventPublisher.publishAll(events)                    │
│                                                         │
│  DepartmentApplicationService                           │
│    aggregate.create() → events                          │
│    eventPublisher.publishAll(events)                    │
└─────────────────────┬───────────────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────────────┐
│              INFRASTRUCTURE LAYER                        │
│                                                         │
│  KafkaDomainEventPublisher                              │
│    implements DomainEventPublisherPort                  │
│    uses KafkaTemplate<String, Object>                   │
│                                                         │
│  ┌───────────────────────────────────────┐              │
│  │  KafkaTopicConfig                     │              │
│  │    NewTopic: employee-events (3 part) │              │
│  │    NewTopic: department-events (3 p.) │              │
│  │    NewTopic: audit-events (1 part)    │              │
│  └───────────────────────────────────────┘              │
│                                                         │
│  ┌───────────────────────────────────────┐              │
│  │  KafkaConsumerConfig                  │              │
│  │    employeeKafkaListenerContainerFactory              │
│  │    departmentKafkaListenerContainerFactory            │
│  │    auditKafkaListenerContainerFactory │              │
│  └───────────────────────────────────────┘              │
└─────────────────────┬───────────────────────────────────┘
                      │ KafkaTemplate.send()
┌─────────────────────▼───────────────────────────────────┐
│                  KAFKA BROKER                           │
│                                                         │
│  ┌─────────────────┐  ┌──────────────────┐             │
│  │ employee-events │  │department-events │             │
│  │  P0 | P1 | P2   │  │  P0 | P1 | P2   │             │
│  └────────┬────────┘  └────────┬─────────┘             │
│           │                    │                        │
│  ┌────────┴────────────────────┴──────────┐            │
│  │           audit-events (P0)            │            │
│  └────────────────────────────────────────┘            │
└─────────────────────┬───────────────────────────────────┘
                      │ @KafkaListener
┌─────────────────────▼───────────────────────────────────┐
│               CONSUMER LAYER                            │
│                                                         │
│  EmployeeEventConsumer   → logs employee events         │
│  DepartmentEventConsumer → logs department events       │
│  AuditEventConsumer      → logs all events              │
└─────────────────────────────────────────────────────────┘
```

## Event Flow Diagram

```
POST /api/v1/enterprise/employees
          │
          ▼
EnterpriseEmployeeController
          │
          ▼
EmployeeApplicationService.handle(CreateEmployeeCommand)
          │
          ▼
EmployeeFactory.create(...)
          │
          ▼
EmployeeAggregate (raises EmployeeCreatedEvent)
          │
          ▼
employeeRepository.save(aggregate)
          │
          ▼
eventPublisher.publishAll(aggregate.getDomainEvents())
          │
          ▼ [DomainEventPublisherPort.publish(EmployeeCreatedEvent)]
          │
          ▼
KafkaDomainEventPublisher
  ├── builds EmployeeEventMessage record
  └── kafkaTemplate.send("employee-events", eventId, message)
                │
                ▼
    Apache Kafka: employee-events topic
                │
          ┌─────┴────────────────┐
          ▼                      ▼
EmployeeEventConsumer      AuditEventConsumer
  logs EMPLOYEE_CREATED      logs audit entry
```

## Package Structure

```
src/main/java/com/act/intern/employeedirectory/enterprise/
│
├── domain/                          ← Pure Java, NO Spring, NO Kafka
│   ├── shared/
│   │   └── event/DomainEvent.java   ← Base event class
│   ├── employee/
│   │   ├── aggregate/EmployeeAggregate.java
│   │   └── event/
│   │       ├── EmployeeCreatedEvent.java
│   │       ├── EmployeeUpdatedEvent.java
│   │       └── EmployeeDeletedEvent.java
│   └── department/
│       └── event/
│           ├── DepartmentCreatedEvent.java
│           ├── DepartmentUpdatedEvent.java
│           └── DepartmentDeletedEvent.java
│
├── application/                     ← Orchestration, ports defined here
│   └── shared/port/
│       └── DomainEventPublisherPort.java  ← Interface (Port)
│
├── infrastructure/                  ← Kafka lives HERE only
│   ├── event/kafka/
│   │   └── KafkaDomainEventPublisher.java  ← implements Port
│   ├── messaging/
│   │   ├── consumer/
│   │   │   ├── EmployeeEventConsumer.java
│   │   │   ├── DepartmentEventConsumer.java
│   │   │   └── AuditEventConsumer.java
│   │   └── dto/
│   │       ├── EmployeeEventMessage.java
│   │       ├── DepartmentEventMessage.java
│   │       └── AuditEventMessage.java
│   └── adapter/out/messaging/
│       └── KafkaEventPublishingAdapter.java  ← Hexagonal marker
│
└── configuration/
    ├── KafkaTopicConfig.java        ← Auto-creates topics
    └── KafkaConsumerConfig.java     ← Typed consumer factories
```

## DDD Compliance

| Principle | How Enforced |
|-----------|-------------|
| Domain independence | `domain` package has zero Kafka imports |
| Port isolation | `DomainEventPublisherPort` is in `application.shared.port` |
| Adapter implementation | `KafkaDomainEventPublisher` is in `infrastructure` |
| Event sourcing | `EmployeeAggregate.getDomainEvents()` collects events |
| Event clearing | `aggregate.clearDomainEvents()` after publishing |

## Kafka Configuration Reference

| Property | Default | Override |
|----------|---------|---------|
| Bootstrap servers | `localhost:9092` | `KAFKA_BOOTSTRAP_SERVERS` env var |
| Consumer group | `employee-directory-group` | `application.yml` |
| Auto offset reset | `earliest` | `application.yml` |
| Value serializer | `JsonSerializer` | `application.yml` |
| Trusted packages | DTO package | `application.yml` |
