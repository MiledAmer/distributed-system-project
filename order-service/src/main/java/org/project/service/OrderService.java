package org.project.service;

import java.util.UUID;

import org.project.domain.OrderEntity;
import org.project.repository.OrderRepository;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    private final OrderRepository repository;

    public OrderService(OrderRepository repository) {
        this.repository = repository;
    }

    public OrderEntity createOrder(String sagaId, String customerId, double amount) {

        OrderEntity order = new OrderEntity(
                UUID.randomUUID().toString(),
                sagaId,
                customerId,
                amount,
                "APPROVAL_PENDING");

        return repository.save(order);
    }

    public OrderEntity updateStatus(String orderId, String sagaId, String status) {

        OrderEntity order = repository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(status);

        return repository.save(order);
    }
}
