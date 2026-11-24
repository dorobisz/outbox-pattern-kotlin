package com.example.outbox.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "outbox_events")
class OutboxEventEntity(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(name = "aggregate_type", nullable = false, length = 100)
    val aggregateType: String,

    @Column(name = "aggregate_id", nullable = false)
    val aggregateId: String,

    @Column(name = "event_type", nullable = false, length = 100)
    val eventType: String,

    @Column(name = "payload", nullable = false, columnDefinition = "TEXT")
    val payload: String,

    @Column(name = "processed", nullable = false)
    var processed: Boolean = false,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),

    @Column(name = "processed_at")
    var processedAt: Instant? = null,
) {
    fun markProcessed(now: Instant = Instant.now()) {
        processed = true
        processedAt = now
    }
}
