package com.lld.phase2.solid.srp.problem2;

import com.lld.phase2.solid.stubs.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

// TODO: SRP VIOLATION — controller is doing 4 distinct things:
//   1. Stock validation (business rule)
//   2. Tax calculation (business rule)
//   3. Persistence (data concern)
//   4. Email confirmation (side-effect concern)
//
// Controller's ONLY job should be: parse HTTP request → call service → return HTTP response.
// Your task: extract OrderService, OrderEmailSender, PricingCalculator.
// Then reduce this controller to a single delegating @PostMapping method.

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderRepository orderRepository;
    private final StockRepository stockRepository;
    private final EmailClient emailClient;

    public OrderController(OrderRepository orderRepository,
                           StockRepository stockRepository,
                           EmailClient emailClient) {
        this.orderRepository = orderRepository;
        this.stockRepository = stockRepository;
        this.emailClient     = emailClient;
    }

    @PostMapping
    public ResponseEntity<Order> placeOrder(@RequestBody OrderRequest req) {
        // Business rule 1: stock validation — should NOT be in controller
        if (req.getQuantity() > stockRepository.getAvailable(req.getProductId())) {
            throw new InsufficientStockException("Not enough stock for: " + req.getProductId());
        }

        // Business rule 2: tax calculation — should NOT be in controller
        BigDecimal tax   = req.getPrice().multiply(BigDecimal.valueOf(0.18));
        BigDecimal total = req.getPrice().add(tax);

        // Data concern: persistence — should delegate to service
        Order order = new Order(req.getProductId(), req.getQuantity(), total);
        orderRepository.save(order);

        // Side-effect: email — should NOT be in controller
        String body = "Your order #" + order.getId() + " is confirmed. Total: ₹" + total;
        emailClient.send(req.getEmail(), "Order Confirmed", body);

        return ResponseEntity.ok(order);
    }
}
