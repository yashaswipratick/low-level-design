package com.lld.phase2.solid.ocp.problem1.fix.strategy;

import com.lld.phase2.solid.stubs.Order;

import java.math.BigDecimal;

public interface DiscountStrategy {

    BigDecimal apply(Order order);
}
