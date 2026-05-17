package org.project.service;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import org.project.domain.AccountEntity;
import org.project.repository.AccountRepository;
import org.project.account.*;
import org.project.account.AccountServiceGrpc.AccountServiceImplBase;

@GrpcService
public class AccountService extends AccountServiceImplBase {
    private final AccountRepository repository;

    public AccountService(AccountRepository repository) {
        this.repository = repository;
    }

    @Override
    public void authorizePayment(AuthorizationRequest request, StreamObserver<AuthorizationResponse> responseObserver) {
        try {
            String customerId = request.getCustomerId();
            double amount = request.getAmount();
            String orderId = request.getOrderId();

            var accountOptional = repository.findById(customerId);

            AuthorizationStatus status;
            boolean success = false;

            if (accountOptional.isEmpty()) {
                status = AuthorizationStatus.REJECTED;
            } else if (accountOptional.isPresent() && accountOptional.get().getAmount() >= amount) {
                AccountEntity account = accountOptional.get();
                account.setBalance(account.getAmount() - amount);
                account.setStatus("AUTHORIZED");
                repository.save(account);
                status = AuthorizationStatus.ACCEPTED;
                success = true;
            } else {
                status = AuthorizationStatus.REJECTED;
            }

            AuthorizationResponse response = AuthorizationResponse.newBuilder()
                    .setOrderId(orderId)
                    .setStatus(status)
                    .setSuccess(success)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }
}
