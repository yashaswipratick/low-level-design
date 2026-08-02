package com.lld.phase6.patterns.behavioral.strategy.problem3.impl;

import com.lld.phase6.patterns.behavioral.strategy.problem3.TaxStrategy;

public class IndiaTaxStrategy implements TaxStrategy {

    private static final double GST_RATE = 0.18;

    @Override
    public double calculate(double amount) {
        return amount * GST_RATE;
    }

    @Override
    public String getCountry() {
        return "INDIA";
    }
}
