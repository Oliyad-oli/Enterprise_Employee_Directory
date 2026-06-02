package com.act.intern.employeedirectory.enterprise.domain.employee.specification;

import com.act.intern.employeedirectory.enterprise.domain.employee.aggregate.EmployeeAggregate;
import com.act.intern.employeedirectory.enterprise.domain.employee.valueobject.Salary;

import java.math.BigDecimal;

public class EmployeeSpecification {
    public static boolean isSalaryInRange(EmployeeAggregate employee, BigDecimal min, BigDecimal max) {
        return employee.getSalary().isInRange(Salary.of(min), Salary.of(max));
    }
    public static boolean isActive(EmployeeAggregate employee) {
        return employee.getStatus().isActive() && !employee.isDeleted();
    }
    public static boolean belongsToDepartment(EmployeeAggregate employee, Long departmentId) {
        return employee.getDepartmentId().equals(departmentId);
    }
}
