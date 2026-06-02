package com.act.intern.employeedirectory.enterprise.domain.employee.event;

import com.act.intern.employeedirectory.enterprise.domain.shared.event.DomainEvent;

public class EmployeeUpdatedEvent extends DomainEvent {
    private final Long employeeId;
    public EmployeeUpdatedEvent(Long employeeId) {
        super("Employee", "EMPLOYEE_UPDATED");
        this.employeeId = employeeId;
    }
    public Long getEmployeeId() { return employeeId; }
}
