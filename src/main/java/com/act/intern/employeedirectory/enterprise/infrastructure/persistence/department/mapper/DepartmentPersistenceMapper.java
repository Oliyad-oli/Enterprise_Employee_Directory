package com.act.intern.employeedirectory.enterprise.infrastructure.persistence.department.mapper;

import com.act.intern.employeedirectory.enterprise.domain.department.aggregate.DepartmentAggregate;
import com.act.intern.employeedirectory.enterprise.domain.shared.valueobject.AuditInfo;
import com.act.intern.employeedirectory.enterprise.infrastructure.persistence.department.entity.DepartmentJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class DepartmentPersistenceMapper {

    public DepartmentJpaEntity toEntity(DepartmentAggregate aggregate) {
        DepartmentJpaEntity entity = new DepartmentJpaEntity();
        if (aggregate.getId() != null) entity.setId(aggregate.getId().value());
        entity.setName(aggregate.getName().value());
        entity.setDescription(aggregate.getDescription());
        entity.setDeleted(aggregate.isDeleted());
        entity.setVersion(aggregate.getVersion());
        return entity;
    }

    public DepartmentAggregate toDomain(DepartmentJpaEntity entity) {
        AuditInfo auditInfo = new AuditInfo(
            entity.getCreatedAt(), entity.getUpdatedAt(),
            entity.getCreatedBy(), entity.getUpdatedBy()
        );
        return DepartmentAggregate.reconstitute(
            entity.getId(), entity.getName(), entity.getDescription(),
            auditInfo, entity.isDeleted(), entity.getVersion()
        );
    }
}
