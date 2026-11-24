package com.example.outbox.config

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling

@Configuration
@EnableScheduling
@ConditionalOnProperty(
    name = ["outbox.publisher.enabled"],
    havingValue = "true",
    matchIfMissing = true,
)
class SchedulingConfig
