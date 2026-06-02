package com.act.intern.employeedirectory.enterprise.domain.department.event;

import com.act.intern.employeedirectory.enterprise.domain.shared.event.DomainEvent;

public class DepartmentUpdatedEvent extends DomainEvent {
    private final Long departmentId;
    public DepartmentUpdatedEvent(Long departmentId) {
        super("Department", "DEPARTMENT_UPDATED");
        this.departmentId = departmentId;
    }
    public Long getDepartmentId() { return departmentId; }
}
