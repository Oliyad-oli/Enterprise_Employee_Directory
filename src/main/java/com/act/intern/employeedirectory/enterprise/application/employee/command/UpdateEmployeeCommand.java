package com.act.intern.employeedirectory.enterprise.application.employee.command;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateEmployeeCommand(
    Long employeeId,
    String firstName,
    String lastName,
    String email,
    BigDecimal salary,
    LocalDate hireDate,
    Long departmentId,
    String requestedBy
) {}
