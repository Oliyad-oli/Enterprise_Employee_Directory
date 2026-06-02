package com.act.intern.employeedirectory.enterprise.infrastructure.event.audit;

import com.act.intern.employeedirectory.enterprise.application.shared.port.AuditLogPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class Slf4jAuditLogAdapter implements AuditLogPort {

    @Override
    public void log(String action, String entityType, Long entityId, String performedBy, String details) {
        log.info("[AUDIT] action={} entityType={} entityId={} performedBy={} details={}",
            action, entityType, entityId, performedBy, details);
    }
}
