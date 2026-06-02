package com.act.intern.employeedirectory.enterprise.domain.employee.factory;

import com.act.intern.employeedirectory.enterprise.domain.employee.aggregate.EmployeeAggregate;
import com.act.intern.employeedirectory.enterprise.domain.employee.valueobject.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public class EmployeeFactory {
    public static EmployeeAggregate create(
            String firstName, String lastName, String email,
            BigDecimal salary, LocalDate hireDate, Long departmentId, String createdBy) {
        return EmployeeAggregate.create(
            EmployeeName.of(firstName, lastName),
            EmployeeEmail.of(email),
            Salary.of(salary),
            hireDate,
            departmentId,
            createdBy
        );
    }
}
