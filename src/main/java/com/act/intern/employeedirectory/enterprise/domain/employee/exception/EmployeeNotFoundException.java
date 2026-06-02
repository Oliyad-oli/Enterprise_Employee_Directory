package com.act.intern.employeedirectory.enterprise.domain.employee.exception;

import com.act.intern.employeedirectory.enterprise.domain.shared.exception.DomainException;

public class EmployeeNotFoundException extends DomainException {
    public EmployeeNotFoundException(Long id) {
        super("Employee not found with id: " + id, "EMPLOYEE_NOT_FOUND");
    }
}
