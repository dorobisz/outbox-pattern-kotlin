package com.example.outbox.outbox

import com.example.outbox.repository.OutboxEventRepository
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class OutboxPublisher(
    private val outboxEventRepository: OutboxEventRepository,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Scheduled(fixedDelayString = "\${outbox.publisher.delay-ms:5000}")
    @Transactional
    fun publishPendingEvents() {
        val pendingEvents = outboxEventRepository.findTop50ByProcessedFalseOrderByCreatedAtAsc()
        if (pendingEvents.isEmpty()) {
            return
        }

        pendingEvents.forEach { event ->
            logger.info(
                "Publishing outbox event: id={}, eventType={}, aggregateType={}, aggregateId={}, payload={}",
                event.id,
                event.eventType,
                event.aggregateType,
                event.aggregateId,
                event.payload,
            )
            event.markProcessed()
        }

        outboxEventRepository.saveAll(pendingEvents)
    }
}
