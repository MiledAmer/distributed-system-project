package org.project.model;

import jakarta.validation.constraints.NotNull;

public record CreateOrderRequest(
        @NotNull String customerId,
        @NotNull double amount) {
}
