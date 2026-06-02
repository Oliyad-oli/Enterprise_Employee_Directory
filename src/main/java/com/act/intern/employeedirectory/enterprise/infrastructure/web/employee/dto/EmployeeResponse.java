package com.act.intern.employeedirectory.enterprise.infrastructure.web.employee.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record EmployeeResponse(
    Long id,
    String firstName,
    String lastName,
    String fullName,
    String email,
    BigDecimal salary,
    LocalDate hireDate,
    String status,
    Long departmentId,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    String createdBy
) {}
