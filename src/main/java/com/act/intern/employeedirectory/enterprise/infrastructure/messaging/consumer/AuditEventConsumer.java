package com.act.intern.employeedirectory.enterprise.infrastructure.messaging.consumer;

import com.act.intern.employeedirectory.enterprise.infrastructure.messaging.dto.AuditEventMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AuditEventConsumer {

    @KafkaListener(
        topics = "${kafka.topics.audit-events}",
        groupId = "employee-directory-audit-group",
        containerFactory = "auditKafkaListenerContainerFactory"
    )
    public void consume(ConsumerRecord<String, AuditEventMessage> record) {
        AuditEventMessage message = record.value();
        log.info("[AUDIT-CONSUMER] ✅ Received audit event: type={} aggregateType={} eventId={}",
            message.eventType(),
            message.aggregateType(),
            message.eventId());
    }
}
