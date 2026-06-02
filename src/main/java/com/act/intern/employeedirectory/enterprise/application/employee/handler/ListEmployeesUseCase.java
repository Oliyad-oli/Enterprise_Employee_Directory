package com.act.intern.employeedirectory.enterprise.application.employee.handler;

import com.act.intern.employeedirectory.enterprise.application.employee.query.ListEmployeesQuery;
import com.act.intern.employeedirectory.enterprise.application.employee.query.SearchEmployeesQuery;
import com.act.intern.employeedirectory.enterprise.domain.employee.aggregate.EmployeeAggregate;

import java.util.List;

public interface ListEmployeesUseCase {
    List<EmployeeAggregate> handle(ListEmployeesQuery query);
    List<EmployeeAggregate> handle(SearchEmployeesQuery query);
}
