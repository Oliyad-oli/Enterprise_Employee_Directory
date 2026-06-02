package com.act.intern.employeedirectory.enterprise.domain.employee.event;

import com.act.intern.employeedirectory.enterprise.domain.shared.event.DomainEvent;

public class EmployeeDeletedEvent extends DomainEvent {
    private final Long employeeId;
    public EmployeeDeletedEvent(Long employeeId) {
        super("Employee", "EMPLOYEE_DELETED");
        this.employeeId = employeeId;
    }
    public Long getEmployeeId() { return employeeId; }
}
