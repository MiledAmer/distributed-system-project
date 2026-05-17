package org.project.controller;

import org.project.model.CreateOrderRequest;
import org.project.model.InitiateOrderResponse;
import org.project.service.OrchestratorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/orders")
public class OrchestratorController {
    private final OrchestratorService orchestratorService;
    private static final Logger logger = LoggerFactory.getLogger(OrchestratorController.class);

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
    public ResponseEntity<String> confirmOrder(
            @PathVariable("orderId") String orderId) {

        boolean success = orchestratorService.confirmOrder(orderId);

        if (success) {
            return ResponseEntity.ok("CONFIRMED");
        }

        return ResponseEntity.ok("FAILED");
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<String> cancelOrder(
            @PathVariable("orderId") String orderId) {

        orchestratorService.cancelOrder(orderId);
        return ResponseEntity.ok("CANCELED");
    }
}
