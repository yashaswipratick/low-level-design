package com.lld.phase2.solid.ocp.problem1.fix.strategy.impl;

import com.lld.phase2.solid.ocp.problem1.fix.strategy.DiscountStrategy;
import com.lld.phase2.solid.stubs.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component("SENIOR_CITIZEN")
public class SeniorCitizenStrategy implements DiscountStrategy {
    @Override
    public BigDecimal apply(Order order) {
        // Premium customers get a 20% discount
        return order.getTotal().multiply(BigDecimal.valueOf(0.15));
    }
}
