package com.act.intern.employeedirectory.enterprise.domain.shared.valueobject;

import java.time.LocalDateTime;
import java.util.Objects;

public record AuditInfo(
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    String createdBy,
    String updatedBy
) {
    public static AuditInfo createNew(String createdBy) {
        LocalDateTime now = LocalDateTime.now();
        return new AuditInfo(now, now, Objects.requireNonNullElse(createdBy, "system"), Objects.requireNonNullElse(createdBy, "system"));
    }
    public AuditInfo withUpdatedBy(String updatedBy) {
        return new AuditInfo(createdAt, LocalDateTime.now(), createdBy, Objects.requireNonNullElse(updatedBy, "system"));
    }
}
