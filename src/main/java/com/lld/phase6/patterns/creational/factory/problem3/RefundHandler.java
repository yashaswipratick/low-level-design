package com.lld.phase6.patterns.creational.factory.problem3;

import com.lld.phase6.patterns.creational.factory.problem3.model.RefundResult;

public interface RefundHandler {
    RefundResult refund(String transactionId, double amount);
}
