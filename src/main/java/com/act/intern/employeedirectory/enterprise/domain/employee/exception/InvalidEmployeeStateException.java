package com.act.intern.employeedirectory.enterprise.domain.employee.exception;

import com.act.intern.employeedirectory.enterprise.domain.shared.exception.DomainException;

public class InvalidEmployeeStateException extends DomainException {
    public InvalidEmployeeStateException(String message) {
        super(message, "INVALID_EMPLOYEE_STATE");
    }
}
