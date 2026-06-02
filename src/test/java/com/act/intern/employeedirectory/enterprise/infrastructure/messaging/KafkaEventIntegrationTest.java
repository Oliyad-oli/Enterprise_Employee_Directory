package com.act.intern.employeedirectory.enterprise.infrastructure.messaging;

import com.act.intern.employeedirectory.enterprise.domain.department.event.DepartmentCreatedEvent;
import com.act.intern.employeedirectory.enterprise.domain.employee.event.EmployeeCreatedEvent;
import com.act.intern.employeedirectory.enterprise.infrastructure.event.kafka.KafkaDomainEventPublisher;
import com.act.intern.employeedirectory.enterprise.infrastructure.messaging.dto.DepartmentEventMessage;
import com.act.intern.employeedirectory.enterprise.infrastructure.messaging.dto.EmployeeEventMessage;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.time.Duration;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Kafka Integration Tests — proves domain events are published and consumed.
 *
 * Test 1: EmployeeCreatedEvent → employee-events topic
 * Test 2: DepartmentCreatedEvent → department-events topic
 * Test 3: Consumer receives messages (verified in Test 1 & 2)
 * Test 4: Kafka topics exist and are accessible
 *
 * Uses EmbeddedKafka — no external Kafka broker required.
 */
@SpringBootTest(classes = {KafkaDomainEventPublisher.class, KafkaAutoConfiguration.class})
@EmbeddedKafka(
    partitions = 1,
    topics = {"employee-events", "department-events", "audit-events"}
)
@TestPropertySource(properties = {
    "kafka.topics.employee-events=employee-events",
    "kafka.topics.department-events=department-events",
    "kafka.topics.audit-events=audit-events",
    "spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer",
    "spring.kafka.producer.properties.spring.json.add.type.headers=false"
})
@DirtiesContext
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class KafkaEventIntegrationTest {

    @Autowired
    private KafkaDomainEventPublisher eventPublisher;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafka;

    // ── Test 1: Employee event published ────────────────────────────────────────

    @Test
    @Order(1)
    void test1_employeeCreatedEvent_publishedTo_employeeEventsTopic() {
        // GIVEN: domain event as produced by EmployeeAggregate
        var event = new EmployeeCreatedEvent(42L, "john@example.com", 1L);

        // WHEN: published through DomainEventPublisherPort → KafkaDomainEventPublisher
        eventPublisher.publish(event);

        // THEN: message arrives on employee-events topic
        try (KafkaConsumer<String, EmployeeEventMessage> consumer = buildConsumer(
                "test-emp-group-1", EmployeeEventMessage.class)) {
            embeddedKafka.consumeFromAnEmbeddedTopic(consumer, "employee-events");
            ConsumerRecords<String, EmployeeEventMessage> records =
                KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(10));

            assertThat(records.count()).isGreaterThanOrEqualTo(1);
            EmployeeEventMessage msg = records.iterator().next().value();
            assertThat(msg.eventType()).isEqualTo("EMPLOYEE_CREATED");
            assertThat(msg.email()).isEqualTo("john@example.com");
            assertThat(msg.employeeId()).isEqualTo(42L);
            assertThat(msg.departmentId()).isEqualTo(1L);
            assertThat(msg.eventId()).isNotBlank();
            assertThat(msg.timestamp()).isNotNull();
        }
    }

    // ── Test 2: Department event published ──────────────────────────────────────

    @Test
    @Order(2)
    void test2_departmentCreatedEvent_publishedTo_departmentEventsTopic() {
        // GIVEN
        var event = new DepartmentCreatedEvent(5L, "Engineering");

        // WHEN
        eventPublisher.publish(event);

        // THEN
        try (KafkaConsumer<String, DepartmentEventMessage> consumer = buildConsumer(
                "test-dept-group-1", DepartmentEventMessage.class)) {
            embeddedKafka.consumeFromAnEmbeddedTopic(consumer, "department-events");
            ConsumerRecords<String, DepartmentEventMessage> records =
                KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(10));

            assertThat(records.count()).isGreaterThanOrEqualTo(1);
            DepartmentEventMessage msg = records.iterator().next().value();
            assertThat(msg.eventType()).isEqualTo("DEPARTMENT_CREATED");
            assertThat(msg.name()).isEqualTo("Engineering");
            assertThat(msg.departmentId()).isEqualTo(5L);
            assertThat(msg.eventId()).isNotBlank();
            assertThat(msg.timestamp()).isNotNull();
        }
    }

    // ── Test 3: Consumer receives messages ──────────────────────────────────────

    @Test
    @Order(3)
    void test3_kafkaConsumer_receivesMessages_fromEmployeeTopic() {
        // GIVEN: two events
        var event1 = new EmployeeCreatedEvent(100L, "alice@example.com", 2L);
        var event2 = new EmployeeCreatedEvent(101L, "bob@example.com", 3L);

        // WHEN
        eventPublisher.publishAll(java.util.List.of(event1, event2));

        // THEN: consumer receives both
        try (KafkaConsumer<String, EmployeeEventMessage> consumer = buildConsumer(
                "test-consumer-group-3", EmployeeEventMessage.class)) {
            embeddedKafka.consumeFromAnEmbeddedTopic(consumer, "employee-events");
            ConsumerRecords<String, EmployeeEventMessage> records =
                KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(10));

            assertThat(records.count()).isGreaterThanOrEqualTo(2);
        }
    }

    // ── Test 4: Kafka topics are created successfully ───────────────────────────

    @Test
    @Order(4)
    void test4_kafkaTopics_areCreated_successfully() {
        Set<String> topics = embeddedKafka.getTopics();
        assertThat(topics).contains("employee-events", "department-events", "audit-events");
    }

    // ── Helper ──────────────────────────────────────────────────────────────────

    private <T> KafkaConsumer<String, T> buildConsumer(String groupId, Class<T> valueType) {
        Map<String, Object> props = KafkaTestUtils.consumerProps(groupId, "true", embeddedKafka);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, valueType.getName());
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        return new KafkaConsumer<>(props);
    }
}
