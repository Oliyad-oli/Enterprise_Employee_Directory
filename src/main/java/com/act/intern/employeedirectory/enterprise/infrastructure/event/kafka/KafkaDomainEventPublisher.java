package com.act.intern.employeedirectory.enterprise.infrastructure.event.kafka;

import com.act.intern.employeedirectory.enterprise.application.shared.port.DomainEventPublisherPort;
import com.act.intern.employeedirectory.enterprise.domain.department.event.DepartmentCreatedEvent;
import com.act.intern.employeedirectory.enterprise.domain.department.event.DepartmentDeletedEvent;
import com.act.intern.employeedirectory.enterprise.domain.department.event.DepartmentUpdatedEvent;
import com.act.intern.employeedirectory.enterprise.domain.employee.event.EmployeeCreatedEvent;
import com.act.intern.employeedirectory.enterprise.domain.employee.event.EmployeeDeletedEvent;
import com.act.intern.employeedirectory.enterprise.domain.employee.event.EmployeeUpdatedEvent;
import com.act.intern.employeedirectory.enterprise.domain.shared.event.DomainEvent;
import com.act.intern.employeedirectory.enterprise.infrastructure.messaging.dto.AuditEventMessage;
import com.act.intern.employeedirectory.enterprise.infrastructure.messaging.dto.DepartmentEventMessage;
import com.act.intern.employeedirectory.enterprise.infrastructure.messaging.dto.EmployeeEventMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * KafkaDomainEventPublisher — Infrastructure adapter implementing DomainEventPublisherPort.
 *
 * Hexagonal role: Outbound port adapter (infrastructure → Kafka broker).
 * The domain never imports this class.
 *
 * Key fixes vs original:
 *   1. KafkaTemplate<String, Object> is now explicitly declared in KafkaProducerConfig
 *      so Spring injects it correctly (not the auto-config String template).
 *   2. send().get(timeout) makes failures visible immediately rather than
 *      silently discarding them in a fire-and-forget callback.
 *   3. AuditEventMessage is also sent synchronously with full error logging.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaDomainEventPublisher implements DomainEventPublisherPort {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.employee-events}")
    private String employeeEventsTopic;

    @Value("${kafka.topics.department-events}")
    private String departmentEventsTopic;

    @Value("${kafka.topics.audit-events}")
    private String auditEventsTopic;

    private static final int PUBLISH_TIMEOUT_SECONDS = 10;

    @Override
    public void publish(DomainEvent event) {
        log.debug("[KAFKA] Routing event: type={} aggregateType={} eventId={}",
            event.getEventType(), event.getAggregateType(), event.getEventId());

        if      (event instanceof EmployeeCreatedEvent   e) publishEmployeeCreated(e);
        else if (event instanceof EmployeeUpdatedEvent   e) publishEmployeeUpdated(e);
        else if (event instanceof EmployeeDeletedEvent   e) publishEmployeeDeleted(e);
        else if (event instanceof DepartmentCreatedEvent e) publishDepartmentCreated(e);
        else if (event instanceof DepartmentUpdatedEvent e) publishDepartmentUpdated(e);
        else if (event instanceof DepartmentDeletedEvent e) publishDepartmentDeleted(e);
        else                                                sendAudit(event);
    }

    @Override
    public void publishAll(List<DomainEvent> events) {
        events.forEach(this::publish);
    }

    // ── Employee events ──────────────────────────────────────────────────────

    private void publishEmployeeCreated(EmployeeCreatedEvent e) {
        var msg = EmployeeEventMessage.of(
            e.getEventId(), e.getEventType(),
            e.getEmployeeId(), e.getEmail(), e.getDepartmentId(), e.getOccurredOn());
        send(employeeEventsTopic, e.getEventId(), msg, e.getEventType());
        sendAudit(e);
    }

    private void publishEmployeeUpdated(EmployeeUpdatedEvent e) {
        var msg = EmployeeEventMessage.of(
            e.getEventId(), e.getEventType(),
            e.getEmployeeId(), null, null, e.getOccurredOn());
        send(employeeEventsTopic, e.getEventId(), msg, e.getEventType());
        sendAudit(e);
    }

    private void publishEmployeeDeleted(EmployeeDeletedEvent e) {
        var msg = EmployeeEventMessage.of(
            e.getEventId(), e.getEventType(),
            e.getEmployeeId(), null, null, e.getOccurredOn());
        send(employeeEventsTopic, e.getEventId(), msg, e.getEventType());
        sendAudit(e);
    }

    // ── Department events ────────────────────────────────────────────────────

    private void publishDepartmentCreated(DepartmentCreatedEvent e) {
        var msg = DepartmentEventMessage.of(
            e.getEventId(), e.getEventType(),
            e.getDepartmentId(), e.getName(), e.getOccurredOn());
        send(departmentEventsTopic, e.getEventId(), msg, e.getEventType());
        sendAudit(e);
    }

    private void publishDepartmentUpdated(DepartmentUpdatedEvent e) {
        var msg = DepartmentEventMessage.of(
            e.getEventId(), e.getEventType(),
            e.getDepartmentId(), null, e.getOccurredOn());
        send(departmentEventsTopic, e.getEventId(), msg, e.getEventType());
        sendAudit(e);
    }

    private void publishDepartmentDeleted(DepartmentDeletedEvent e) {
        var msg = DepartmentEventMessage.of(
            e.getEventId(), e.getEventType(),
            e.getDepartmentId(), null, e.getOccurredOn());
        send(departmentEventsTopic, e.getEventId(), msg, e.getEventType());
        sendAudit(e);
    }

    // ── Core send (synchronous with timeout) ─────────────────────────────────

    private void send(String topic, String key, Object payload, String eventType) {
        try {
            SendResult<String, Object> result = kafkaTemplate
                .send(topic, key, payload)
                .get(PUBLISH_TIMEOUT_SECONDS, TimeUnit.SECONDS);

            log.info("[KAFKA] ✅ Published: type={} topic={} partition={} offset={}",
                eventType, topic,
                result.getRecordMetadata().partition(),
                result.getRecordMetadata().offset());

        } catch (ExecutionException ex) {
            log.error("[KAFKA] ❌ Broker rejected event type={} topic={}: {}",
                eventType, topic, ex.getCause().getMessage(), ex);
            throw new KafkaPublishException("Failed to publish " + eventType + " to " + topic, ex);

        } catch (TimeoutException ex) {
            log.error("[KAFKA] ❌ Timed out publishing event type={} topic={}", eventType, topic);
            throw new KafkaPublishException("Timed out publishing " + eventType, ex);

        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            log.error("[KAFKA] ❌ Interrupted publishing event type={}", eventType);
            throw new KafkaPublishException("Interrupted publishing " + eventType, ex);
        }
    }

    private void sendAudit(DomainEvent event) {
        var msg = new AuditEventMessage(
            event.getEventId(), event.getEventType(),
            event.getAggregateType(), event.getOccurredOn());
        try {
            kafkaTemplate.send(auditEventsTopic, event.getEventId(), msg)
                .get(PUBLISH_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            log.debug("[KAFKA] Audit published: type={}", event.getEventType());
        } catch (Exception ex) {
            // Audit topic failure is non-fatal — log and continue
            log.warn("[KAFKA] Audit publish failed for event type={}: {}",
                event.getEventType(), ex.getMessage());
        }
    }

    // ── Exception ────────────────────────────────────────────────────────────

    public static class KafkaPublishException extends RuntimeException {
        public KafkaPublishException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
