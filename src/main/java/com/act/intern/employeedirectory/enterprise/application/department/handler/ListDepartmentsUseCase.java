package com.act.intern.employeedirectory.enterprise.application.department.handler;

import com.act.intern.employeedirectory.enterprise.application.department.query.ListDepartmentsQuery;
import com.act.intern.employeedirectory.enterprise.domain.department.aggregate.DepartmentAggregate;

import java.util.List;

public interface ListDepartmentsUseCase {
    List<DepartmentAggregate> handle(ListDepartmentsQuery query);
}
