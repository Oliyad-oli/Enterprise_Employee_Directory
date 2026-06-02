package com.act.intern.employeedirectory.enterprise.domain.employee.valueobject;

import java.util.Objects;

public record EmployeeId(Long value) {
    public EmployeeId {
        Objects.requireNonNull(value, "EmployeeId cannot be null");
        if (value <= 0) throw new IllegalArgumentException("EmployeeId must be positive");
    }
    public static EmployeeId of(Long value) { return new EmployeeId(value); }
    @Override public String toString() { return value.toString(); }
}
