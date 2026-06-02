package com.act.intern.employeedirectory.enterprise.domain.department.event;

import com.act.intern.employeedirectory.enterprise.domain.shared.event.DomainEvent;

public class DepartmentDeletedEvent extends DomainEvent {
    private final Long departmentId;
    public DepartmentDeletedEvent(Long departmentId) {
        super("Department", "DEPARTMENT_DELETED");
        this.departmentId = departmentId;
    }
    public Long getDepartmentId() { return departmentId; }
}
