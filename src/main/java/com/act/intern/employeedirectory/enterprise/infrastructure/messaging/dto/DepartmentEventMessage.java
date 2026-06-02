package com.act.intern.employeedirectory.enterprise.infrastructure.messaging.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record DepartmentEventMessage(
    String eventId,
    String eventType,
    Long departmentId,
    String name,
    Instant timestamp
) {
    public static DepartmentEventMessage of(String eventId, String eventType,
                                             Long departmentId, String name, Instant timestamp) {
        return new DepartmentEventMessage(eventId, eventType, departmentId, name, timestamp);
    }
}
