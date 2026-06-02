package com.act.intern.employeedirectory.enterprise.domain.department.valueobject;

import java.util.Objects;

public record DepartmentId(Long value) {
    public DepartmentId { Objects.requireNonNull(value, "DepartmentId cannot be null"); }
    public static DepartmentId of(Long value) { return new DepartmentId(value); }
    @Override public String toString() { return value.toString(); }
}
