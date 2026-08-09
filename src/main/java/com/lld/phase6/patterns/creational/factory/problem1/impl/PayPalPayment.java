package com.lld.phase6.patterns.creational.factory.problem1.impl;

import com.lld.phase6.patterns.creational.factory.problem1.Payment;

public class PayPalPayment implements Payment {

    @Override
    public void processPayment(double amount) {
        System.out.println("Processing PayPal payment of amount: " + amount);
    }

    @Override
    public String getPaymentMethod() {
        return "PayPal";
    }
}
