package com.act.intern.employeedirectory.enterprise.infrastructure.messaging.consumer;

import com.act.intern.employeedirectory.enterprise.infrastructure.messaging.dto.EmployeeEventMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EmployeeEventConsumer {

    @KafkaListener(
        topics = "${kafka.topics.employee-events}",
        groupId = "employee-directory-group",
        containerFactory = "employeeKafkaListenerContainerFactory"
    )
    public void consume(ConsumerRecord<String, EmployeeEventMessage> record) {
        EmployeeEventMessage message = record.value();
        log.info("[CONSUMER] ✅ Received employee event: type={} employeeId={} email={} partition={} offset={}",
            message.eventType(),
            message.employeeId(),
            message.email(),
            record.partition(),
            record.offset());
    }
}
