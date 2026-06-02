package com.act.intern.employeedirectory.enterprise.domain.department.valueobject;

import java.util.Objects;

public record DepartmentName(String value) {
    public DepartmentName {
        Objects.requireNonNull(value, "Department name cannot be null");
        if (value.isBlank()) throw new IllegalArgumentException("Department name cannot be blank");
        if (value.length() > 100) throw new IllegalArgumentException("Department name too long");
        value = value.trim();
    }
    public static DepartmentName of(String value) { return new DepartmentName(value); }
    @Override public String toString() { return value; }
}
