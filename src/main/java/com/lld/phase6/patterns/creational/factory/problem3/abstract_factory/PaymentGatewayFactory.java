package com.lld.phase6.patterns.creational.factory.problem3.abstract_factory;

import com.lld.phase6.patterns.creational.factory.problem3.PaymentProcessor;
import com.lld.phase6.patterns.creational.factory.problem3.RefundHandler;
import com.lld.phase6.patterns.creational.factory.problem3.WebHookParser;

public interface PaymentGatewayFactory {

    PaymentProcessor createProcessor();
    RefundHandler createRefundHandler();
    WebHookParser createWebhookParser();
}
