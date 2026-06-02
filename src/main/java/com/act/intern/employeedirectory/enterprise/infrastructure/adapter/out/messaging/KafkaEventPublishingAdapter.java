package com.act.intern.employeedirectory.enterprise.infrastructure.adapter.out.messaging;

/**
 * Hexagonal Architecture — Outbound Messaging Adapter.
 *
 * This package contains the Kafka adapter that implements {@code DomainEventPublisherPort}.
 * Per hexagonal architecture:
 *   - The domain defines the Port (interface)
 *   - This adapter provides the Kafka implementation
 *   - The domain never knows about Kafka
 *
 * The actual implementation lives in:
 *   {@link com.act.intern.employeedirectory.enterprise.infrastructure.event.kafka.KafkaDomainEventPublisher}
 *
 * Architecture flow:
 *   Domain Event
 *     → Application Service
 *       → DomainEventPublisherPort (interface)
 *         → KafkaDomainEventPublisher (this adapter)
 *           → KafkaTemplate
 *             → Kafka Topic
 *               → Event Consumers
 */
public final class KafkaEventPublishingAdapter {
    // Marker class — see package-info and KafkaDomainEventPublisher for implementation.
    private KafkaEventPublishingAdapter() {}
}
