package com.lld.phase6.patterns.behavioral.strategy.problem3.impl;

import com.lld.phase6.patterns.behavioral.strategy.problem3.TaxStrategy;

public class GermanyTaxStrategy implements TaxStrategy {

    private static final double VAT_RATE = 0.19;

    @Override
    public double calculate(double amount) {
        return amount * VAT_RATE;
    }

    @Override
    public String getCountry() {
        return "GERMANY";
    }
}
