package com.example.outbox.service

import java.time.Instant
import java.util.UUID

data class OrderCreatedEvent(
    val orderId: UUID,
    val customerId: String,
    val createdAt: Instant,
)
