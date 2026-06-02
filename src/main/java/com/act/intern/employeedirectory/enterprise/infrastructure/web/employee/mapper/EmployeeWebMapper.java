package com.act.intern.employeedirectory.enterprise.infrastructure.web.employee.mapper;

import com.act.intern.employeedirectory.enterprise.domain.employee.aggregate.EmployeeAggregate;
import com.act.intern.employeedirectory.enterprise.infrastructure.web.employee.dto.EmployeeResponse;
import org.springframework.stereotype.Component;

@Component
public class EmployeeWebMapper {
    public EmployeeResponse toResponse(EmployeeAggregate aggregate) {
        return new EmployeeResponse(
            aggregate.getId() != null ? aggregate.getId().value() : null,
            aggregate.getName().firstName(),
            aggregate.getName().lastName(),
            aggregate.getName().fullName(),
            aggregate.getEmail().value(),
            aggregate.getSalary().amount(),
            aggregate.getHireDate(),
            aggregate.getStatus().name(),
            aggregate.getDepartmentId(),
            aggregate.getAuditInfo() != null ? aggregate.getAuditInfo().createdAt() : null,
            aggregate.getAuditInfo() != null ? aggregate.getAuditInfo().updatedAt() : null,
            aggregate.getAuditInfo() != null ? aggregate.getAuditInfo().createdBy() : null
        );
    }
}
