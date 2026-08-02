package com.lld.phase2.solid.ocp.problem1;

import com.lld.phase2.solid.ocp.problem1.fix.strategy.DiscountServiceOrchestrator;
import com.lld.phase2.solid.ocp.problem1.fix.strategy.DiscountStrategy;
import com.lld.phase2.solid.ocp.problem1.fix.strategy.impl.EmployeeStrategy;
import com.lld.phase2.solid.ocp.problem1.fix.strategy.impl.PremiumStrategy;
import com.lld.phase2.solid.ocp.problem1.fix.strategy.impl.SeniorCitizenStrategy;
import com.lld.phase2.solid.ocp.problem1.fix.strategy.impl.StudentStrategy;
import com.lld.phase2.solid.stubs.Order;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class Driver {
    static Map<String, DiscountStrategy> strategyMap;
    public static void init() {
        strategyMap = new HashMap<>();
        strategyMap.put("PREMIUM", new PremiumStrategy());
        strategyMap.put("STUDENT", new StudentStrategy());
        strategyMap.put("SENIOR_CITIZEN", new SeniorCitizenStrategy());
        strategyMap.put("EMPLOYEE", new EmployeeStrategy());
    }
    public static void main(String[] args) {
        init();
        DiscountServiceOrchestrator discountServiceOrchestrator = new DiscountServiceOrchestrator(strategyMap);
        System.out.println(discountServiceOrchestrator.calculateDiscounts(new Order("prod_01", 10, BigDecimal.TEN), "PREMIUM"));
    }
}
