package com.example.outbox

import com.example.outbox.outbox.OutboxPublisher
import com.example.outbox.repository.OrderRepository
import com.example.outbox.repository.OutboxEventRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@SpringBootTest
@AutoConfigureMockMvc
class OutboxApplicationTests {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var orderRepository: OrderRepository

    @Autowired
    private lateinit var outboxEventRepository: OutboxEventRepository

    @Autowired
    private lateinit var outboxPublisher: OutboxPublisher

    @BeforeEach
    fun cleanUp() {
        outboxEventRepository.deleteAll()
        orderRepository.deleteAll()
    }

    @Test
    fun `POST orders creates order and unprocessed outbox event in single flow`() {
        mockMvc.post("/orders") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"customerId": "123"}"""
        }.andExpect {
            status { isCreated() }
            jsonPath("$.customerId") { value("123") }
            jsonPath("$.id") { isNotEmpty() }
            jsonPath("$.createdAt") { isNotEmpty() }
        }

        val orders = orderRepository.findAll()
        assertEquals(1, orders.size)
        assertEquals("123", orders.first().customerId)

        val events = outboxEventRepository.findAll()
        assertEquals(1, events.size)
        val event = events.first()
        assertEquals("Order", event.aggregateType)
        assertEquals(orders.first().id.toString(), event.aggregateId)
        assertEquals("OrderCreated", event.eventType)
        assertFalse(event.processed)
        assertTrue(event.payload.contains("\"customerId\":\"123\""))
    }

    @Test
    fun `POST orders rejects blank customerId`() {
        mockMvc.post("/orders") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"customerId": "   "}"""
        }.andExpect {
            status { isBadRequest() }
        }

        assertEquals(0, orderRepository.count())
        assertEquals(0, outboxEventRepository.count())
    }

    @Test
    fun `OutboxPublisher marks pending events as processed`() {
        mockMvc.post("/orders") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"customerId": "456"}"""
        }.andExpect {
            status { isCreated() }
        }

        assertEquals(1, outboxEventRepository.findTop50ByProcessedFalseOrderByCreatedAtAsc().size)

        outboxPublisher.publishPendingEvents()

        val events = outboxEventRepository.findAll()
        assertEquals(1, events.size)
        assertTrue(events.first().processed)
        assertNotNull(events.first().processedAt)
        assertEquals(0, outboxEventRepository.findTop50ByProcessedFalseOrderByCreatedAtAsc().size)
    }
}
