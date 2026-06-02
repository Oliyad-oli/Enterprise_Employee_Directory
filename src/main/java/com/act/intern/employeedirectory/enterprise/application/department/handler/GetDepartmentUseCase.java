package com.act.intern.employeedirectory.enterprise.application.department.handler;

import com.act.intern.employeedirectory.enterprise.application.department.query.GetDepartmentQuery;
import com.act.intern.employeedirectory.enterprise.domain.department.aggregate.DepartmentAggregate;

public interface GetDepartmentUseCase {
    DepartmentAggregate handle(GetDepartmentQuery query);
}
