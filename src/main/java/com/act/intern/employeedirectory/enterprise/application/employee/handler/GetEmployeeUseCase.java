package com.act.intern.employeedirectory.enterprise.application.employee.handler;

import com.act.intern.employeedirectory.enterprise.application.employee.query.GetEmployeeQuery;
import com.act.intern.employeedirectory.enterprise.domain.employee.aggregate.EmployeeAggregate;

public interface GetEmployeeUseCase {
    EmployeeAggregate handle(GetEmployeeQuery query);
}
