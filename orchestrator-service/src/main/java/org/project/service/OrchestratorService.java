package org.project.service;

import org.project.client.*;
import org.project.kitchen.TicketStatus;
import org.project.account.AuthorizationStatus;
import org.project.order.OrderStatus;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OrchestratorService {
    private final OrderClient orderClient;
    private final KitchenClient kitchenClient;
    private final AccountClient accountClient;

    // temporary state (saga tracking)
    private final Map<String, PendingOrder> pending = new ConcurrentHashMap<>();

    private record PendingOrder(String orderId, String customerId, double amount) {
    }

    public OrchestratorService(OrderClient orderClient,
            KitchenClient kitchenClient,
            AccountClient accountClient) {
        this.orderClient = orderClient;
        this.kitchenClient = kitchenClient;
        this.accountClient = accountClient;
    }

    public String initiateOrder(String customerId, double amount) {

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

        pending.put(orderId, new PendingOrder(orderId, customerId, amount));

        return orderId;
    }

    // STEP 2: confirm order (payment + finalize)
    public boolean confirmOrder(String orderId) {

        PendingOrder order = pending.remove(orderId);

        if (order == null)
            return false;

        var payment = accountClient.authorize(
                orderId,
                order.customerId(),
                order.amount());

        if (!payment.getSuccess() ||
                payment.getStatus() != AuthorizationStatus.ACCEPTED) {

            rollback(orderId);
            return false;
        }

        orderClient.updateStatus(orderId, OrderStatus.APPROVED);
        kitchenClient.acceptTicket(orderId);

        return true;
    }

    // STEP 3: cancel order
    public void cancelOrder(String orderId) {

        pending.remove(orderId);
        rollback(orderId);
    }

    // COMPENSATION LOGIC
    private void rollback(String orderId) {

        orderClient.updateStatus(orderId, OrderStatus.CANCELED);
        kitchenClient.rejectTicket(orderId);
    }
}
