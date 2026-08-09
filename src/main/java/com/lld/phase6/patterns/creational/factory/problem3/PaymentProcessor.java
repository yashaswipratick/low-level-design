package com.lld.phase6.patterns.creational.factory.problem3;

import com.lld.phase6.patterns.creational.factory.problem3.model.PaymentResult;

public interface PaymentProcessor {

    PaymentResult charge(String customerId, double amount);
}
