package org.project.client;

import io.grpc.ManagedChannel;
import org.project.account.*;

public class AccountClient {
    private final AccountServiceGrpc.AccountServiceBlockingStub stub;

    public AccountClient(ManagedChannel channel) {
        this.stub = AccountServiceGrpc.newBlockingStub(channel);
    }

    public AuthorizationResponse authorize(String orderId, String customerId, double amount) {
        return stub.authorizePayment(
                AuthorizationRequest.newBuilder()
                        .setOrderId(orderId)
                        .setCustomerId(customerId)
                        .setAmount(amount)
                        .build());
    }
}
