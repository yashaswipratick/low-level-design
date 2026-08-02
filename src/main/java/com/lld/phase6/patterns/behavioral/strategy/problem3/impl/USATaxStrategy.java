package com.lld.phase6.patterns.behavioral.strategy.problem3.impl;

import com.lld.phase6.patterns.behavioral.strategy.problem3.TaxStrategy;

public class USATaxStrategy implements TaxStrategy {

    private static final double SALES_TAX_RATE = 0.08;

    @Override
    public double calculate(double amount) {
        return amount * SALES_TAX_RATE;
    }

    @Override
    public String getCountry() {
        return "USA";
    }
}
