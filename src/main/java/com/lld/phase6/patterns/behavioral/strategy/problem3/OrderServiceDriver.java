package com.lld.phase6.patterns.behavioral.strategy.problem3;

import com.lld.phase6.patterns.behavioral.strategy.problem3.impl.GermanyTaxStrategy;
import com.lld.phase6.patterns.behavioral.strategy.problem3.impl.IndiaTaxStrategy;
import com.lld.phase6.patterns.behavioral.strategy.problem3.impl.UKTaxStrategy;
import com.lld.phase6.patterns.behavioral.strategy.problem3.impl.USATaxStrategy;

public class OrderServiceDriver {

    public static void main(String[] args) {
        String item      = "Laptop";
        double basePrice = 1000.0;

        new OrderService(new IndiaTaxStrategy()).printOrderSummary(item, basePrice);
        new OrderService(new USATaxStrategy()).printOrderSummary(item, basePrice);
        new OrderService(new GermanyTaxStrategy()).printOrderSummary(item, basePrice);
        new OrderService(new UKTaxStrategy()).printOrderSummary(item, basePrice);
    }
}

