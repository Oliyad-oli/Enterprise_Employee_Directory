package com.act.intern.employeedirectory.enterprise.infrastructure.web.employee.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateEmployeeRequest(
    @NotBlank(message = "First name is required") String firstName,
    @NotBlank(message = "Last name is required") String lastName,
    @NotBlank(message = "Email is required") @Email(message = "Invalid email format") String email,
    @NotNull(message = "Salary is required") @Positive(message = "Salary must be positive") BigDecimal salary,
    @NotNull(message = "Hire date is required") LocalDate hireDate,
    @NotNull(message = "Department ID is required") Long departmentId
) {}
