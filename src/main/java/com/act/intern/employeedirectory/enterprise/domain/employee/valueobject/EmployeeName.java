package com.act.intern.employeedirectory.enterprise.domain.employee.valueobject;

import java.util.Objects;

public record EmployeeName(String firstName, String lastName) {
    public EmployeeName {
        Objects.requireNonNull(firstName, "First name cannot be null");
        Objects.requireNonNull(lastName, "Last name cannot be null");
        if (firstName.isBlank()) throw new IllegalArgumentException("First name cannot be blank");
        if (lastName.isBlank()) throw new IllegalArgumentException("Last name cannot be blank");
        firstName = firstName.trim();
        lastName = lastName.trim();
    }
    public static EmployeeName of(String firstName, String lastName) { return new EmployeeName(firstName, lastName); }
    public String fullName() { return firstName + " " + lastName; }
}
