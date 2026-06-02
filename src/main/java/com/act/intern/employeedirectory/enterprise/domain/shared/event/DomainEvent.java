package com.act.intern.employeedirectory.enterprise.domain.shared.event;

import java.time.Instant;
import java.util.UUID;

public abstract class DomainEvent {
    private final String eventId;
    private final Instant occurredOn;
    private final String aggregateType;
    private final String eventType;

    protected DomainEvent(String aggregateType, String eventType) {
        this.eventId = UUID.randomUUID().toString();
        this.occurredOn = Instant.now();
        this.aggregateType = aggregateType;
        this.eventType = eventType;
    }

    public String getEventId() { return eventId; }
    public Instant getOccurredOn() { return occurredOn; }
    public String getAggregateType() { return aggregateType; }
    public String getEventType() { return eventType; }
}
