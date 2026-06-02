package com.act.intern.employeedirectory.enterprise.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Explicit Kafka producer configuration.
 *
 * Root cause of the silent-publish bug:
 *   Spring Boot auto-config produces KafkaTemplate<String, String>.
 *   KafkaDomainEventPublisher declares KafkaTemplate<String, Object>.
 *   Spring injects by type — no match → bean injection fails silently
 *   OR falls back to wrong serializer → messages corrupt / dropped.
 *
 * Fix: declare an explicit ProducerFactory<String, Object> and
 *   KafkaTemplate<String, Object> so injection is unambiguous and the
 *   JsonSerializer is guaranteed to handle any domain event DTO.
 */
@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,     bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,  StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        // Reliability settings
        props.put(ProducerConfig.ACKS_CONFIG,                          "all");
        props.put(ProducerConfig.RETRIES_CONFIG,                       3);
        props.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG,              1000);
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG,            true);
        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 1);

        // Do NOT embed Java type headers — consumers use explicit target type
        props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);

        var factory = new DefaultKafkaProducerFactory<String, Object>(props);

        // Use a shared ObjectMapper that handles Java 8 date/time types
        ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        factory.setValueSerializer(new JsonSerializer<>(mapper));

        return factory;
    }

    /**
     * This bean is what KafkaDomainEventPublisher injects.
     * Declaring it explicitly prevents Spring Boot from injecting
     * the default KafkaTemplate<String, String> instead.
     */
    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
