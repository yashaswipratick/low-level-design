package com.lld.phase6.patterns.behavioral.observer.problem2.impl;

import com.lld.phase6.patterns.behavioral.observer.problem2.StockObserver;

import java.util.HashMap;
import java.util.Map;

public class PortfolioTracker implements StockObserver {
    private final Map<String, Double> holdings;
    private Map<String, Double> prices = new HashMap<>();

    public PortfolioTracker(Map<String, Double> holdings) {
        this.holdings = holdings;
    }

    @Override
    public void onPriceChange(String stockSymbol, double oldPrice, double newPrice) {
        prices.put(stockSymbol, newPrice);
        double total = holdings.entrySet().stream()
                .mapToDouble(e -> e.getValue() * prices.getOrDefault(e.getKey(), 0.0))
                .sum();
        System.out.printf("[Portfolio] %s: $%.2f → $%.2f | Total value: $%.2f%n",
                stockSymbol, oldPrice, newPrice, total);
    }
}
