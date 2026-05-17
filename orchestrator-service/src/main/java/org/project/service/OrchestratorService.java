package org.project.service;

import org.project.client.*;
import org.project.kitchen.TicketStatus;
import org.project.account.AuthorizationStatus;
import org.project.order.OrderStatus;
import org.project.model.InitiateOrderResponse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class OrchestratorService {

    private static final Logger logger = LoggerFactory.getLogger(OrchestratorService.class);
    private final OrderClient orderClient;
    private final KitchenClient kitchenClient;
    private final AccountClient accountClient;

    // temporary state (saga tracking)
    private final Map<String, PendingOrder> pending = new ConcurrentHashMap<>();

    private record PendingOrder(String orderId, String ticketId, String customerId, double amount) {
    }

    public OrchestratorService(OrderClient orderClient,
            KitchenClient kitchenClient,
            AccountClient accountClient) {
        this.orderClient = orderClient;
        this.kitchenClient = kitchenClient;
        this.accountClient = accountClient;
    }

    public InitiateOrderResponse initiateOrder(String customerId, double amount) {

        var orderResponse = orderClient.createOrder(customerId, amount);

        if (!orderResponse.getSuccess()) {
            return null;
        }

        String orderId = orderResponse.getOrderId();

        var kitchenResponse = kitchenClient.createTicket(orderId, customerId);

        if (kitchenResponse.getStatus() == TicketStatus.REJECTED
                || kitchenResponse.getStatus() == TicketStatus.CANCELED) {

            orderClient.updateStatus(orderId, OrderStatus.CANCELED);
            return null;
        }

        pending.put(orderId, new PendingOrder(orderId, kitchenResponse.getTicketId(), customerId, amount));

        return new InitiateOrderResponse(
                orderId,
                orderResponse.getStatus().name(),
                kitchenResponse.getTicketId(),
                kitchenResponse.getStatus().name());
    }

    // STEP 2: confirm order (payment + finalize)
    public boolean confirmOrder(String orderId) {
        PendingOrder order = pending.remove(orderId);

        if (order == null)
            return false;

        logger.info("pre payment authorization for orderId: {}, customerId: {}, amount: {}",
                order.orderId(), order.customerId(), order.amount());

        var payment = accountClient.authorize(
                orderId,
                order.customerId(),
                order.amount());
        logger.info("post payment authorization for orderId: {}, success: {}, status: {}",
                orderId, payment.getSuccess(), payment.getStatus());

        if (!payment.getSuccess() ||
                payment.getStatus() != AuthorizationStatus.ACCEPTED) {

            rollback(orderId);
            return false;
        }

        orderClient.updateStatus(orderId, OrderStatus.APPROVED);

        logger.info("Order status updated to APPROVED for orderId: {}", orderId);

        kitchenClient.acceptTicket(order.ticketId());
        logger.info("Ticket accepted for orderId: {}", orderId);

        return true;
    }

    // STEP 3: cancel order
    public void cancelOrder(String orderId) {

        pending.remove(orderId);
        rollback(orderId);
    }

    // COMPENSATION LOGIC
    private void rollback(String orderId) {
        logger.warn("Starting rollback for orderId: {}", orderId);

        try {
            orderClient.updateStatus(orderId, OrderStatus.CANCELED);
            logger.info("Order status updated to CANCELED for orderId: {}", orderId);
        } catch (Exception e) {
            logger.error("Failed to update order status during rollback for orderId: {}", orderId, e);
        }

        try {
            kitchenClient.cancelTicket(orderId);
            logger.info("Ticket canceled for orderId: {}", orderId);
        } catch (Exception e) {
            logger.error("Failed to cancel ticket during rollback for orderId: {}", orderId, e);
        }
    }
}
