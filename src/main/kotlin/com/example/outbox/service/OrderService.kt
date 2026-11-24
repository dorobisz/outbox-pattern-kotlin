package com.example.outbox.service

import com.example.outbox.domain.OrderEntity
import com.example.outbox.domain.OutboxEventEntity
import com.example.outbox.repository.OrderRepository
import com.example.outbox.repository.OutboxEventRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import tools.jackson.databind.ObjectMapper

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val outboxEventRepository: OutboxEventRepository,
    private val objectMapper: ObjectMapper,
) {
    @Transactional
    fun createOrder(customerId: String): OrderEntity {
        val order = orderRepository.save(OrderEntity(customerId = customerId))

        val eventPayload = OrderCreatedEvent(
            orderId = order.id,
            customerId = order.customerId,
            createdAt = order.createdAt,
        )

        val outboxEvent = OutboxEventEntity(
            aggregateType = "Order",
            aggregateId = order.id.toString(),
            eventType = "OrderCreated",
            payload = objectMapper.writeValueAsString(eventPayload),
        )
        outboxEventRepository.save(outboxEvent)

        return order
    }
}
