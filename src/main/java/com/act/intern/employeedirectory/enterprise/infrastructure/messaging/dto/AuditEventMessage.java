package com.act.intern.employeedirectory.enterprise.infrastructure.messaging.dto;

import java.time.Instant;

public record AuditEventMessage(
    String eventId,
    String eventType,
    String aggregateType,
    Instant timestamp
) {}
