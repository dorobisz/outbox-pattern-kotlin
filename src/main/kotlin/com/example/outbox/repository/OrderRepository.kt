package com.example.outbox.repository

import com.example.outbox.domain.OrderEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface OrderRepository : JpaRepository<OrderEntity, UUID>
