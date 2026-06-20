package com.example.asyncrunner.api.dto;

import com.example.asyncrunner.domain.OrderStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderResponse(
    UUID id,
    UUID customerId,
    OrderStatus status,
    BigDecimal totalAmount,
    UUID taskId,
    Instant createdAt
) {}