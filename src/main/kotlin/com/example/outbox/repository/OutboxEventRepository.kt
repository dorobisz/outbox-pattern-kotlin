package com.example.outbox.repository

import com.example.outbox.domain.OutboxEventEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface OutboxEventRepository : JpaRepository<OutboxEventEntity, UUID> {
    fun findTop50ByProcessedFalseOrderByCreatedAtAsc(): List<OutboxEventEntity>
}
