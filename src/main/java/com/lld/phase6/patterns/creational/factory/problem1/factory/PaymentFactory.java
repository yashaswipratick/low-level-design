package com.lld.phase6.patterns.creational.factory.problem1.factory;

import com.lld.phase6.patterns.creational.factory.problem1.Payment;
import com.lld.phase6.patterns.creational.factory.problem1.PaymentType;
import com.lld.phase6.patterns.creational.factory.problem1.impl.CreditCardPayment;
import com.lld.phase6.patterns.creational.factory.problem1.impl.CryptoPayment;
import com.lld.phase6.patterns.creational.factory.problem1.impl.PayPalPayment;

public class PaymentFactory {

    public static Payment create(PaymentType paymentType) {
        return switch (paymentType) {
            case CREDIT_CARD -> new CreditCardPayment();
            case PAYPAL -> new PayPalPayment();
            case CRYPTO -> new CryptoPayment();
        };
    }
}
