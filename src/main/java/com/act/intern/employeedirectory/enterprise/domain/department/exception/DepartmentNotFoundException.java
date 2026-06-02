package com.act.intern.employeedirectory.enterprise.domain.department.exception;

import com.act.intern.employeedirectory.enterprise.domain.shared.exception.DomainException;

public class DepartmentNotFoundException extends DomainException {
    public DepartmentNotFoundException(Long id) {
        super("Department not found with id: " + id, "DEPARTMENT_NOT_FOUND");
    }
}
