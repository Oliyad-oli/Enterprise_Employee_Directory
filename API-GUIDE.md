# API Guide

## Standard Response Format

```json
{
  "success": true,
  "message": "Operation completed successfully",
  "timestamp": "2026-01-01T10:00:00",
  "data": {}
}
```

## Error Response (RFC 7807 Problem Details)

```json
{
  "type": "https://api.employee-directory.com/errors/not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Employee not found with id: 999",
  "errorCode": "EMPLOYEE_NOT_FOUND",
  "timestamp": "2026-01-01T10:00:00"
}
```

## Validation Error Response

```json
{
  "success": false,
  "message": "Validation failed",
  "errors": [
    { "field": "email", "message": "Invalid email format" },
    { "field": "salary", "message": "Salary must be positive" }
  ]
}
```

## HTTP Status Codes

| Code | Meaning |
|---|---|
| 200 | Success |
| 201 | Created |
| 204 | No Content (delete) |
| 400 | Bad Request / Validation Error |
| 404 | Not Found |
| 409 | Conflict (duplicate) |
| 422 | Unprocessable Entity |
| 500 | Internal Server Error |
