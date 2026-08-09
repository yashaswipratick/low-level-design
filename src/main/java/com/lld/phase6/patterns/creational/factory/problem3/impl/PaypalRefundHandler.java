package com.lld.phase6.patterns.creational.factory.problem3.impl;

import com.lld.phase6.patterns.creational.factory.problem3.RefundHandler;
import com.lld.phase6.patterns.creational.factory.problem3.model.RefundResult;

public class PaypalRefundHandler implements RefundHandler {
    @Override
    public RefundResult refund(String transactionId, double amount) {
        System.out.println("Refunding paypal transaction " + transactionId + " an amount of " + amount + " using Paypal.");
        return new RefundResult(true, "ref001" ,"Paypal refund successful");
    }
}
