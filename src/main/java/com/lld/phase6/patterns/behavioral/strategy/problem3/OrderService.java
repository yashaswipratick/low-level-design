package com.lld.phase6.patterns.behavioral.strategy.problem3;

public class OrderService {

    private TaxStrategy  taxStrategy;

    public OrderService(TaxStrategy taxStrategy) {
        this.taxStrategy = taxStrategy;
    }

    public void printOrderSummary(String item, double basePrice) {
        double tax   = taxStrategy.calculate(basePrice);
        double total = basePrice + tax;
        System.out.printf("[%s] %s: Base=%.2f Tax=%.2f Total=%.2f%n",
                taxStrategy.getCountry(), item, basePrice, tax, total);
    }
}
