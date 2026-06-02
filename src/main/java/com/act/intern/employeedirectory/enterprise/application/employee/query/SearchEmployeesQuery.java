package com.act.intern.employeedirectory.enterprise.application.employee.query;

import java.math.BigDecimal;

public record SearchEmployeesQuery(
    String keyword,
    BigDecimal minSalary,
    BigDecimal maxSalary,
    int page,
    int size,
    String sortBy
) {}
