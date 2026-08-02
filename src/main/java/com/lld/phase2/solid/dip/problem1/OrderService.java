package com.lld.phase2.solid.dip.problem1;

import com.lld.phase2.solid.stubs.*;
import com.lld.phase2.stubs.*;
import org.springframework.stereotype.Service;

// TODO: DIP VIOLATION — high-level business service depends on concrete low-level implementations.
//
// Problems:
//   1. API keys are hardcoded in source code — security risk, never commit these
//   2. Switching from SendGrid to AWS SES requires modifying this class
//   3. Switching from Twilio to Vonage requires modifying this class
//   4. Unit testing requires real HTTP calls to SendGrid and Twilio
//   5. `new ConcreteClass()` bypasses Spring IoC — no lifecycle, no mock injection
//
// Your task:
//   1. Define EmailSender and SmsSender interfaces
//   2. Implement SendGridEmailSender and TwilioSmsSender as @Components
//   3. Move credentials to @Value("${...}") properties — never in source
//   4. OrderService constructor accepts only interfaces — never concrete classes
//   5. Write OrderServiceTest using mocks: new OrderService(mockRepo, mockEmail, mockSms)

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    // VIOLATION: hardwired concrete implementations with credentials in source
    private final SendGridEmailClient emailClient = new SendGridEmailClient(
        "SG.hardcoded-api-key-do-not-commit",
        "noreply@myshop.com"
    );

    // VIOLATION: hardwired concrete implementation
    private final TwilioSmsClient smsClient = new TwilioSmsClient(
        "AC-twilio-account-sid",
        "twilio-auth-token-hardcoded"
    );

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order placeOrder(OrderRequest request) {
        Order order = Order.create(
            request.getProductId(),
            request.getQuantity(),
            request.getPrice()
        );
        orderRepository.save(order);

        // VIOLATION: directly calls concrete vendor APIs
        emailClient.send(
            request.getCustomerEmail(),
            "Order Confirmed — #" + order.getId()
        );

        smsClient.send(
            request.getCustomerPhone(),
            "Your order #" + order.getId() + " has been placed!"
        );

        return order;
    }
}
