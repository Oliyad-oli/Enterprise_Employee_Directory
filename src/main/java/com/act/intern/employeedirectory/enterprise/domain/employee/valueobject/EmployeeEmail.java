package com.act.intern.employeedirectory.enterprise.domain.employee.valueobject;

import java.util.Objects;
import java.util.regex.Pattern;

public record EmployeeEmail(String value) {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    public EmployeeEmail {
        Objects.requireNonNull(value, "Email cannot be null");
        if (!EMAIL_PATTERN.matcher(value).matches()) throw new IllegalArgumentException("Invalid email format: " + value);
        value = value.toLowerCase().trim();
    }
    public static EmployeeEmail of(String value) { return new EmployeeEmail(value); }
    @Override public String toString() { return value; }
}
