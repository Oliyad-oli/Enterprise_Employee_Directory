package com.act.intern.employeedirectory.enterprise.infrastructure.messaging.consumer;

import com.act.intern.employeedirectory.enterprise.infrastructure.messaging.dto.DepartmentEventMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DepartmentEventConsumer {

    @KafkaListener(
        topics = "${kafka.topics.department-events}",
        groupId = "employee-directory-group",
        containerFactory = "departmentKafkaListenerContainerFactory"
    )
    public void consume(ConsumerRecord<String, DepartmentEventMessage> record) {
        DepartmentEventMessage message = record.value();
        log.info("[CONSUMER] ✅ Received department event: type={} departmentId={} name={} partition={} offset={}",
            message.eventType(),
            message.departmentId(),
            message.name(),
            record.partition(),
            record.offset());
    }
}
