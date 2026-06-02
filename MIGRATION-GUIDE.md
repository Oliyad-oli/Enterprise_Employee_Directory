# Migration Guide

## Flyway Migration Versions

| Version | File | Purpose |
|---|---|---|
| V1 | V1__create_departments_table.sql | Original departments table |
| V2 | V2__create_employees_table.sql | Original employees table |
| V3 | V3__create_enterprise_departments_table.sql | Enterprise departments with audit/soft-delete |
| V4 | V4__create_enterprise_employees_table.sql | Enterprise employees with audit/soft-delete/status |

## Enterprise Table Features

- `deleted` (BOOLEAN) — soft delete flag
- `deleted_at` (TIMESTAMP) — when deleted
- `version` (INTEGER) — optimistic locking
- `created_at`, `updated_at` — JPA `@CreatedDate`, `@LastModifiedDate`
- `created_by`, `updated_by` — JPA `@CreatedBy`, `@LastModifiedBy`

## Coexistence

Both implementations run simultaneously. Flyway applies all migrations in version order. Original tables are untouched.
