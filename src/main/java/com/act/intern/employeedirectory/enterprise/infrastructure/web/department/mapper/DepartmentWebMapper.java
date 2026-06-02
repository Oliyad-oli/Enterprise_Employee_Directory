package com.act.intern.employeedirectory.enterprise.infrastructure.web.department.mapper;

import com.act.intern.employeedirectory.enterprise.domain.department.aggregate.DepartmentAggregate;
import com.act.intern.employeedirectory.enterprise.infrastructure.web.department.dto.DepartmentResponse;
import org.springframework.stereotype.Component;

@Component
public class DepartmentWebMapper {
    public DepartmentResponse toResponse(DepartmentAggregate aggregate) {
        return new DepartmentResponse(
            aggregate.getId() != null ? aggregate.getId().value() : null,
            aggregate.getName().value(),
            aggregate.getDescription(),
            aggregate.getAuditInfo() != null ? aggregate.getAuditInfo().createdAt() : null,
            aggregate.getAuditInfo() != null ? aggregate.getAuditInfo().updatedAt() : null,
            aggregate.getAuditInfo() != null ? aggregate.getAuditInfo().createdBy() : null
        );
    }
}
