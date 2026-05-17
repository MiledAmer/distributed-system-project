package org.project.model;

public record CreateOrderResponse(
        String orderId,
        String status) {
}