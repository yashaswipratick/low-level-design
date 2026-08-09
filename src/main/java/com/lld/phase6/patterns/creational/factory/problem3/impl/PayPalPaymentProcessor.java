package com.lld.phase6.patterns.creational.factory.problem3.impl;

import com.lld.phase6.patterns.creational.factory.problem3.PaymentProcessor;
import com.lld.phase6.patterns.creational.factory.problem3.model.PaymentResult;

public class PayPalPaymentProcessor implements PaymentProcessor {
    @Override
    public PaymentResult charge(String customerId, double amount) {
        // Implement Stripe-specific charging logic here
        System.out.println("Charging Paypal customer " + customerId + " an amount of " + amount + " using Stripe.");
        return new PaymentResult(true, "tran001" ,"Paypal charge successful");
    }
}
