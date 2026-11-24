package com.example.outbox.api

import com.example.outbox.service.OrderService
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.time.Instant
import java.util.UUID

data class CreateOrderRequest(
    @field:NotBlank(message = "customerId must not be blank")
    val customerId: String,
)

data class OrderResponse(
    val id: UUID,
    val customerId: String,
    val createdAt: Instant,
)

@RestController
@RequestMapping("/orders")
class OrderController(
    private val orderService: OrderService,
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createOrder(@Valid @RequestBody request: CreateOrderRequest): OrderResponse {
        val order = orderService.createOrder(request.customerId)
        return OrderResponse(
            id = order.id,
            customerId = order.customerId,
            createdAt = order.createdAt,
        )
    }
}
