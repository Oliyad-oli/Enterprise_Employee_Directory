package com.act.intern.employeedirectory.enterprise.application.shared.port;

import com.act.intern.employeedirectory.enterprise.domain.shared.event.DomainEvent;
import java.util.List;

public interface DomainEventPublisherPort {
    void publish(DomainEvent event);
    void publishAll(List<DomainEvent> events);
}
