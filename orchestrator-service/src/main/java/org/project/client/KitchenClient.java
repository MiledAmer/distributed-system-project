package org.project.client;

import io.grpc.ManagedChannel;
import org.project.kitchen.*;

public class KitchenClient {
    private final KitchenServiceGrpc.KitchenServiceBlockingStub stub;

    public KitchenClient(ManagedChannel channel) {
        this.stub = KitchenServiceGrpc.newBlockingStub(channel);
    }

    public CreateTicketResponse createTicket(String orderId, String customerId) {
        return stub.createTicket(
                CreateTicketRequest.newBuilder()
                        .setTicketId(orderId)
                        .setCustomerId(customerId)
                        .build());
    }

    public void acceptTicket(String orderId) {
        stub.acceptTicket(
                AcceptTicketRequest.newBuilder()
                        .setTicketId(orderId)
                        .build());
    }

    public void rejectTicket(String orderId) {
        stub.rejectTicket(
                RejectTicketRequest.newBuilder()
                        .setTicketId(orderId)
                        .build());
    }

    public void cancelTicket(String orderId) {
        stub.cancelTicket(
                CancelTicketRequest.newBuilder()
                        .setTicketId(orderId)
                        .build());
    }
}
