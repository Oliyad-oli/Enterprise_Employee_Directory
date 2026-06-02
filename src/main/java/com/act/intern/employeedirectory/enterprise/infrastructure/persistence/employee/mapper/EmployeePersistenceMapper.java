package com.act.intern.employeedirectory.enterprise.infrastructure.persistence.employee.mapper;

import com.act.intern.employeedirectory.enterprise.domain.employee.aggregate.EmployeeAggregate;
import com.act.intern.employeedirectory.enterprise.domain.shared.valueobject.AuditInfo;
import com.act.intern.employeedirectory.enterprise.infrastructure.persistence.employee.entity.EmployeeJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class EmployeePersistenceMapper {

    public EmployeeJpaEntity toEntity(EmployeeAggregate aggregate) {
        EmployeeJpaEntity entity = new EmployeeJpaEntity();
        if (aggregate.getId() != null) entity.setId(aggregate.getId().value());
        entity.setFirstName(aggregate.getName().firstName());
        entity.setLastName(aggregate.getName().lastName());
        entity.setEmail(aggregate.getEmail().value());
        entity.setSalary(aggregate.getSalary().amount());
        entity.setHireDate(aggregate.getHireDate());
        entity.setStatus(aggregate.getStatus().name());
        entity.setDepartmentId(aggregate.getDepartmentId());
        entity.setDeleted(aggregate.isDeleted());
        entity.setVersion(aggregate.getVersion());
        return entity;
    }

    public EmployeeAggregate toDomain(EmployeeJpaEntity entity) {
        AuditInfo auditInfo = new AuditInfo(
            entity.getCreatedAt(), entity.getUpdatedAt(),
            entity.getCreatedBy(), entity.getUpdatedBy()
        );
        return EmployeeAggregate.reconstitute(
            entity.getId(), entity.getFirstName(), entity.getLastName(),
            entity.getEmail(), entity.getSalary(), entity.getHireDate(),
            entity.getStatus(), entity.getDepartmentId(),
            auditInfo, entity.isDeleted(), entity.getVersion()
        );
    }
}
