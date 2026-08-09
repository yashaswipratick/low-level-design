package com.lld.phase6.patterns.creational.factory.problem3;

import com.lld.phase6.patterns.creational.factory.problem3.abstract_factory.PaymentGatewayFactory;
import com.lld.phase6.patterns.creational.factory.problem3.model.PaymentResult;
import com.lld.phase6.patterns.creational.factory.problem3.model.RefundResult;
import com.lld.phase6.patterns.creational.factory.problem3.model.WebhookEvent;

public class PaymentService {

    private final PaymentGatewayFactory factory;

    public PaymentService(PaymentGatewayFactory factory) {
        this.factory = factory;
    }

    public void charge(String customerId, double amount) {
        PaymentProcessor processor = factory.createProcessor();
        PaymentResult charge = processor.charge(customerId, amount);
        System.out.println("charge result:" + charge);
    }

    public void refund(String transactionId, double amount) {
        RefundHandler refundHandler = factory.createRefundHandler();
        RefundResult result = refundHandler.refund(transactionId, amount);
        System.out.println("refund result:" + result);
    }

    public void handleWebHook(String payload) {
        WebHookParser webhookParser = factory.createWebhookParser();
        WebhookEvent result = webhookParser.parse(payload);
        System.out.println("webhook result:" + result);
    }
}
