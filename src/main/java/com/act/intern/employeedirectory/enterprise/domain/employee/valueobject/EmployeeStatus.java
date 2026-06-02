package com.act.intern.employeedirectory.enterprise.domain.employee.valueobject;

public enum EmployeeStatus {
    ACTIVE,
    INACTIVE,
    ON_LEAVE,
    TERMINATED;

    public boolean isActive() { return this == ACTIVE; }
    public boolean canBeDeleted() { return this == INACTIVE || this == TERMINATED; }
}
