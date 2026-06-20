package com.example.asyncrunner.api.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateOrderRequest(
    @NotNull(message = "Customer ID is required")
    UUID customerId
) {}