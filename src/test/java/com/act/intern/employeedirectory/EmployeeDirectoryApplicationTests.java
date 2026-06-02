package com.act.intern.employeedirectory;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

/**
 * Verifies Spring context loads successfully (Test 5 from requirements).
 * Uses EmbeddedKafka (no external broker) + H2 (no external PostgreSQL).
 */
@SpringBootTest
@EmbeddedKafka(
    partitions = 1,
    topics = {"employee-events", "department-events", "audit-events"}
)
@DirtiesContext
class EmployeeDirectoryApplicationTests {

    @Test
    void contextLoads() {
        // All beans wired: DDD domain, application services, Kafka adapters,
        // JPA adapters, REST controllers — zero missing dependencies.
    }
}
