package com.act.intern.employeedirectory.enterprise.domain.department.exception;

import com.act.intern.employeedirectory.enterprise.domain.shared.exception.DomainException;

public class DuplicateDepartmentNameException extends DomainException {
    public DuplicateDepartmentNameException(String name) {
        super("Department with name already exists: " + name, "DUPLICATE_DEPARTMENT_NAME");
    }
}
