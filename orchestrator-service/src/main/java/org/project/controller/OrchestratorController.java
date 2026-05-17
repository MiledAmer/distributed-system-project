package org.project.controller;

import org.project.model.CreateOrderRequest;
import org.project.model.InitiateOrderResponse;
import org.project.model.PaymentResponse;
import org.project.service.OrchestratorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/orders")
public class OrchestratorController {
    private final OrchestratorService orchestratorService;

    public OrchestratorController(OrchestratorService orchestratorService) {
        this.orchestratorService = orchestratorService;
    }

    // STEP 1: create order (start saga)
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody CreateOrderRequest request) {

        InitiateOrderResponse createdOrder = orchestratorService.initiateOrder(
                request.customerId(),
                request.amount());

        if (createdOrder == null) {
            return ResponseEntity.badRequest().body("ORDER_CREATION_FAILED");
        }

        return ResponseEntity.ok(createdOrder);
    }

    @PostMapping("/{orderId}/confirm")
    public ResponseEntity<?> confirmOrder(
            @PathVariable("orderId") String orderId) {

        PaymentResponse payment = orchestratorService.confirmOrder(orderId);

        if (payment != null) {
            return ResponseEntity.ok(payment);
        }

        return ResponseEntity.badRequest().body("PAYMENT_NOT_CREATED_OR_REJECTED");
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<String> cancelOrder(
            @PathVariable("orderId") String orderId) {

        orchestratorService.cancelOrder(orderId);
        return ResponseEntity.ok("CANCELED");
    }
}
