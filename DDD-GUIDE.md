# Domain-Driven Design Guide

## Bounded Contexts

### 1. Employee Management Context
- **Aggregate Root**: `EmployeeAggregate`
- **Value Objects**: `EmployeeId`, `EmployeeEmail`, `EmployeeName`, `Salary`, `EmployeeStatus`
- **Domain Events**: `EmployeeCreatedEvent`, `EmployeeUpdatedEvent`, `EmployeeDeletedEvent`
- **Exceptions**: `EmployeeNotFoundException`, `DuplicateEmployeeEmailException`, `InvalidEmployeeStateException`
- **Factory**: `EmployeeFactory`
- **Specification**: `EmployeeSpecification`

### 2. Department Management Context
- **Aggregate Root**: `DepartmentAggregate`
- **Value Objects**: `DepartmentId`, `DepartmentName`
- **Domain Events**: `DepartmentCreatedEvent`, `DepartmentUpdatedEvent`, `DepartmentDeletedEvent`
- **Exceptions**: `DepartmentNotFoundException`, `DuplicateDepartmentNameException`
- **Factory**: `DepartmentFactory`

## Business Rules (enforced in aggregates)
- Email must be unique and valid
- Salary must be positive and below maximum
- Deleted employees cannot be updated
- Department names are case-insensitive unique
- Soft-delete sets status to TERMINATED

## Value Object Invariants
All value objects are Java records that validate in compact constructors.
They are immutable and self-validating.
