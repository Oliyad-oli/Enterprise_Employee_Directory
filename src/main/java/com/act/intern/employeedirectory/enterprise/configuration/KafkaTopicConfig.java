package com.act.intern.employeedirectory.enterprise.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${kafka.topics.employee-events}")
    private String employeeEventsTopic;

    @Value("${kafka.topics.department-events}")
    private String departmentEventsTopic;

    @Value("${kafka.topics.audit-events}")
    private String auditEventsTopic;

    @Bean
    public NewTopic employeeEventsTopic() {
        return TopicBuilder.name(employeeEventsTopic)
            .partitions(3)
            .replicas(1)
            .build();
    }

    @Bean
    public NewTopic departmentEventsTopic() {
        return TopicBuilder.name(departmentEventsTopic)
            .partitions(3)
            .replicas(1)
            .build();
    }

    @Bean
    public NewTopic auditEventsTopic() {
        return TopicBuilder.name(auditEventsTopic)
            .partitions(1)
            .replicas(1)
            .build();
    }
}
