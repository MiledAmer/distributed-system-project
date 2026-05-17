package org.project.controller;

import org.project.model.CreateOrderRequest;
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
    public ResponseEntity<String> createOrder(@RequestBody CreateOrderRequest request) {

        String orderId = orchestratorService.initiateOrder(
                request.customerId(),
                request.amount());

        if (orderId == null) {
            return ResponseEntity.badRequest().body("ORDER_CREATION_FAILED");
        }

        return ResponseEntity.ok(orderId);
    }

    // STEP 2: confirm order (payment + finalize saga)
    @PostMapping("/{orderId}/confirm")
    public ResponseEntity<String> confirmOrder(@PathVariable String orderId) {

        boolean success = orchestratorService.confirmOrder(orderId);

        if (success) {
            return ResponseEntity.ok("CONFIRMED");
        }

        return ResponseEntity.ok("FAILED");
    }

    // STEP 3: cancel order (compensation)
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<String> cancelOrder(@PathVariable String orderId) {

        orchestratorService.cancelOrder(orderId);

        return ResponseEntity.ok("CANCELED");
    }
}
