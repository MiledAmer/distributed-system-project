package org.project.model;

public record PaymentResponse(
        String orderId,
        String status,
        boolean success) {
}