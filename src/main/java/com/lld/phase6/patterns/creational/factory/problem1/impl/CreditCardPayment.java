package com.lld.phase6.patterns.creational.factory.problem1.impl;

import com.lld.phase6.patterns.creational.factory.problem1.Payment;

public class CreditCardPayment implements Payment {

    @Override
    public void processPayment(double amount) {
        System.out.println("Processing credit card payment of amount: " + amount);
    }

    @Override
    public String getPaymentMethod() {
        return "CreditCard";
    }
}
