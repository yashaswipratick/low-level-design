package com.lld.phase6.patterns.creational.factory.problem3.factory;

import com.lld.phase6.patterns.creational.factory.problem3.PaymentProcessor;
import com.lld.phase6.patterns.creational.factory.problem3.RefundHandler;
import com.lld.phase6.patterns.creational.factory.problem3.WebHookParser;
import com.lld.phase6.patterns.creational.factory.problem3.abstract_factory.PaymentGatewayFactory;
import com.lld.phase6.patterns.creational.factory.problem3.impl.*;

public class PaypalGatewayFactory implements PaymentGatewayFactory {
    @Override
    public PaymentProcessor createProcessor() {
        return new PayPalPaymentProcessor();
    }

    @Override
    public RefundHandler createRefundHandler() {
        return new PaypalRefundHandler();
    }

    @Override
    public WebHookParser createWebhookParser() {
       return new PaypalWebHookParser();
    }
}
