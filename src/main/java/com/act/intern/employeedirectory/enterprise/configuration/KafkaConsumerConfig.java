package com.act.intern.employeedirectory.enterprise.configuration;

import com.act.intern.employeedirectory.enterprise.infrastructure.messaging.dto.AuditEventMessage;
import com.act.intern.employeedirectory.enterprise.infrastructure.messaging.dto.DepartmentEventMessage;
import com.act.intern.employeedirectory.enterprise.infrastructure.messaging.dto.EmployeeEventMessage;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    private Map<String, Object> baseConsumerProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return props;
    }

    // ── Employee ────────────────────────────────────────────────────────────

    @Bean
    public ConsumerFactory<String, EmployeeEventMessage> employeeConsumerFactory() {
        Map<String, Object> props = baseConsumerProps();
        JsonDeserializer<EmployeeEventMessage> deserializer =
            new JsonDeserializer<>(EmployeeEventMessage.class, false);
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, EmployeeEventMessage>
    employeeKafkaListenerContainerFactory() {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, EmployeeEventMessage>();
        factory.setConsumerFactory(employeeConsumerFactory());
        return factory;
    }

    // ── Department ──────────────────────────────────────────────────────────

    @Bean
    public ConsumerFactory<String, DepartmentEventMessage> departmentConsumerFactory() {
        Map<String, Object> props = baseConsumerProps();
        JsonDeserializer<DepartmentEventMessage> deserializer =
            new JsonDeserializer<>(DepartmentEventMessage.class, false);
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, DepartmentEventMessage>
    departmentKafkaListenerContainerFactory() {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, DepartmentEventMessage>();
        factory.setConsumerFactory(departmentConsumerFactory());
        return factory;
    }

    // ── Audit ───────────────────────────────────────────────────────────────

    @Bean
    public ConsumerFactory<String, AuditEventMessage> auditConsumerFactory() {
        Map<String, Object> props = baseConsumerProps();
        JsonDeserializer<AuditEventMessage> deserializer =
            new JsonDeserializer<>(AuditEventMessage.class, false);
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AuditEventMessage>
    auditKafkaListenerContainerFactory() {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, AuditEventMessage>();
        factory.setConsumerFactory(auditConsumerFactory());
        return factory;
    }
}
