package org.project.grpc;

import io.grpc.stub.StreamObserver;
import org.project.order.*;

import org.project.service.OrderService;
import org.project.domain.OrderEntity;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class OrderServiceImpl extends OrderServiceGrpc.OrderServiceImplBase {
    private final OrderService orderService;

    public OrderServiceImpl(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public void createOrder(CreateOrderRequest request,
            StreamObserver<CreateOrderResponse> responseObserver) {

        OrderEntity order = orderService.createOrder(
                request.getSagaId(),
                request.getCustomerId(),
                request.getAmount());

        CreateOrderResponse response = CreateOrderResponse.newBuilder()
                .setOrderId(order.getId())
                .setCustomerId(order.getCustomerId())
                .setAmount(order.getAmount())
                .setStatus(OrderStatus.APPROVAL_PENDING)
                .setSagaId(order.getSagaId())
                .setSuccess(true)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void updateStatus(UpdateStatusRequest request,
            StreamObserver<UpdateStatusResponse> responseObserver) {

        OrderEntity order = orderService.updateStatus(
                request.getOrderId(),
                request.getSagaId(),
                request.getStatus().name());

        UpdateStatusResponse response = UpdateStatusResponse.newBuilder()
                .setOrderId(order.getId())
                .setNewStatus(OrderStatus.valueOf(order.getStatus()))
                .setSagaId(order.getSagaId())
                .setSuccess(true)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
