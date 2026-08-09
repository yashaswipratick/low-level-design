package com.lld.phase6.patterns.creational.factory.problem3.factory;

import com.lld.phase6.patterns.creational.factory.problem3.PaymentProcessor;
import com.lld.phase6.patterns.creational.factory.problem3.RefundHandler;
import com.lld.phase6.patterns.creational.factory.problem3.WebHookParser;
import com.lld.phase6.patterns.creational.factory.problem3.abstract_factory.PaymentGatewayFactory;
import com.lld.phase6.patterns.creational.factory.problem3.impl.StripePaymentProcessor;
import com.lld.phase6.patterns.creational.factory.problem3.impl.StripeRefundHandler;
import com.lld.phase6.patterns.creational.factory.problem3.impl.StripeWebHookParser;

public class StripeGatewayFactory implements PaymentGatewayFactory {
    @Override
    public PaymentProcessor createProcessor() {
        return new StripePaymentProcessor();
    }

    @Override
    public RefundHandler createRefundHandler() {
        return new StripeRefundHandler();
    }

    @Override
    public WebHookParser createWebhookParser() {
       return new StripeWebHookParser();
    }
}
