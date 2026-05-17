package org.project.client;

import io.grpc.ManagedChannel;
import org.project.order.*;

public class OrderClient {
    private final OrderServiceGrpc.OrderServiceBlockingStub stub;

    public OrderClient(ManagedChannel channel) {
        this.stub = OrderServiceGrpc.newBlockingStub(channel);
    }

    public CreateOrderResponse createOrder(String customerId, double amount) {
        return stub.createOrder(
                CreateOrderRequest.newBuilder()
                        .setCustomerId(customerId)
                        .setAmount(amount)
                        .build());
    }

    public void updateStatus(String orderId, OrderStatus status) {
        stub.updateStatus(
                UpdateStatusRequest.newBuilder()
                        .setOrderId(orderId)
                        .setStatus(status)
                        .build());
    }
}
