package org.project.model;

public record InitiateOrderResponse(
        String orderId,
        String orderStatus,
        String ticketId,
        String ticketStatus) {
}