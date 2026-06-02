package com.act.intern.employeedirectory.enterprise.application.shared.port;

public interface AuditLogPort {
    void log(String action, String entityType, Long entityId, String performedBy, String details);
}
