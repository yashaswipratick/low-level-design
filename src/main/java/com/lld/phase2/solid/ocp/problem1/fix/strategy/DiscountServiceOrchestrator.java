package com.lld.phase2.solid.ocp.problem1.fix.strategy;

import com.lld.phase2.solid.stubs.Order;

import java.math.BigDecimal;
import java.util.Map;

public class DiscountServiceOrchestrator {

    private final Map<String, DiscountStrategy>  strategies;


    public DiscountServiceOrchestrator(Map<String, DiscountStrategy> discountStrategyMap) {
        this.strategies = discountStrategyMap;
    }

    public BigDecimal calculateDiscounts(Order order, String customerType) {
        DiscountStrategy strategy = this.strategies.get(customerType);
        if (strategy == null) {
            return BigDecimal.ZERO;
        }
        return strategy.apply(order);
    }
}
