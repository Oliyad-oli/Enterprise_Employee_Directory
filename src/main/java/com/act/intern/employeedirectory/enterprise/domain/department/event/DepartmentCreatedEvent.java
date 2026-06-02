package com.act.intern.employeedirectory.enterprise.domain.department.event;

import com.act.intern.employeedirectory.enterprise.domain.shared.event.DomainEvent;

public class DepartmentCreatedEvent extends DomainEvent {
    private final Long departmentId;
    private final String name;
    public DepartmentCreatedEvent(Long departmentId, String name) {
        super("Department", "DEPARTMENT_CREATED");
        this.departmentId = departmentId;
        this.name = name;
    }
    public Long getDepartmentId() { return departmentId; }
    public String getName() { return name; }
}
