package com.act.intern.employeedirectory.enterprise.domain.department.factory;

import com.act.intern.employeedirectory.enterprise.domain.department.aggregate.DepartmentAggregate;

public class DepartmentFactory {
    public static DepartmentAggregate create(String name, String description, String createdBy) {
        return DepartmentAggregate.create(name, description, createdBy);
    }
}
