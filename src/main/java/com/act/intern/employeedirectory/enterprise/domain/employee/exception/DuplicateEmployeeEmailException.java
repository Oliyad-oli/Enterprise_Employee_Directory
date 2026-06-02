package com.act.intern.employeedirectory.enterprise.domain.employee.exception;

import com.act.intern.employeedirectory.enterprise.domain.shared.exception.DomainException;

public class DuplicateEmployeeEmailException extends DomainException {
    public DuplicateEmployeeEmailException(String email) {
        super("Employee with email already exists: " + email, "DUPLICATE_EMPLOYEE_EMAIL");
    }
}
