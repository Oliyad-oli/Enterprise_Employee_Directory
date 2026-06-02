package com.act.intern.employeedirectory.enterprise.infrastructure.messaging.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record EmployeeEventMessage(
    String eventId,
    String eventType,
    Long employeeId,
    String email,
    Long departmentId,
    Instant timestamp
) {
    public static EmployeeEventMessage of(String eventId, String eventType,
                                          Long employeeId, String email,
                                          Long departmentId, Instant timestamp) {
        return new EmployeeEventMessage(eventId, eventType, employeeId, email, departmentId, timestamp);
    }
}
