package com.act.intern.employeedirectory.enterprise.domain.employee.valueobject;

import java.math.BigDecimal;
import java.util.Objects;

public record Salary(BigDecimal amount) {
    private static final BigDecimal MIN_SALARY = BigDecimal.valueOf(0);
    private static final BigDecimal MAX_SALARY = BigDecimal.valueOf(10_000_000);
    public Salary {
        Objects.requireNonNull(amount, "Salary cannot be null");
        if (amount.compareTo(MIN_SALARY) <= 0) throw new IllegalArgumentException("Salary must be positive");
        if (amount.compareTo(MAX_SALARY) > 0) throw new IllegalArgumentException("Salary exceeds maximum allowed");
    }
    public static Salary of(BigDecimal amount) { return new Salary(amount); }
    public boolean isInRange(Salary min, Salary max) {
        return amount.compareTo(min.amount) >= 0 && amount.compareTo(max.amount) <= 0;
    }
}
