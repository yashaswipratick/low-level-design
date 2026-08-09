package com.lld.phase6.patterns.creational.factory.problem3.impl;

import com.lld.phase6.patterns.creational.factory.problem3.RefundHandler;
import com.lld.phase6.patterns.creational.factory.problem3.model.RefundResult;

public class StripeRefundHandler implements RefundHandler {
    @Override
    public RefundResult refund(String transactionId, double amount) {
        System.out.println("Refunding Stripe transaction " + transactionId + " an amount of " + amount + " using Stripe.");
        return new RefundResult(true, "ref001" ,"Stripe refund successful");
    }
}
