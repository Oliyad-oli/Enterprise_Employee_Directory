# CQRS Guide

## Commands (Write Side)
| Command | Handler |
|---|---|
| `CreateEmployeeCommand` | `EmployeeApplicationService#handle(CreateEmployeeCommand)` |
| `UpdateEmployeeCommand` | `EmployeeApplicationService#handle(UpdateEmployeeCommand)` |
| `DeleteEmployeeCommand` | `EmployeeApplicationService#handle(DeleteEmployeeCommand)` |
| `CreateDepartmentCommand` | `DepartmentApplicationService#handle(CreateDepartmentCommand)` |
| `UpdateDepartmentCommand` | `DepartmentApplicationService#handle(UpdateDepartmentCommand)` |
| `DeleteDepartmentCommand` | `DepartmentApplicationService#handle(DeleteDepartmentCommand)` |

## Queries (Read Side)
| Query | Handler |
|---|---|
| `GetEmployeeQuery` | `EmployeeApplicationService#handle(GetEmployeeQuery)` |
| `ListEmployeesQuery` | `EmployeeApplicationService#handle(ListEmployeesQuery)` |
| `SearchEmployeesQuery` | `EmployeeApplicationService#handle(SearchEmployeesQuery)` |
| `GetDepartmentQuery` | `DepartmentApplicationService#handle(GetDepartmentQuery)` |
| `ListDepartmentsQuery` | `DepartmentApplicationService#handle(ListDepartmentsQuery)` |

## Flow
```
HTTP Request → REST Controller → Command/Query → Use Case → Domain → Repository → DB
                                                         ↓
                                               Domain Events → Kafka Publisher
```
