package com.lld.phase6.patterns.creational.factory.problem1;

import com.lld.phase6.patterns.creational.factory.problem1.factory.PaymentFactory;

public class PaymentService {

    public void pay(PaymentType paymentType, double amount) {
        Payment payment = PaymentFactory.create(paymentType);
        payment.processPayment(amount);
        System.out.println("Method: " + payment.getPaymentMethod());
    }
}
