package com.lld.phase6.patterns.behavioral.observer.problem2.impl;

import com.lld.phase6.patterns.behavioral.observer.problem2.PriceDirection;
import com.lld.phase6.patterns.behavioral.observer.problem2.StockObserver;

public class PriceAlertNotifier implements StockObserver {
    private final String ticker;
    private final double threshold;
    private final PriceDirection direction;

    public PriceAlertNotifier(String ticker, double threshold, PriceDirection direction) {
        this.ticker = ticker;
        this.threshold = threshold;
        this.direction = direction;
    }

    @Override
    public void onPriceChange(String stockSymbol, double oldPrice, double newPrice) {
        if (!stockSymbol.equals(ticker)) return;
        boolean triggered = direction.equals(PriceDirection.ABOVE) ? newPrice > threshold : newPrice < threshold;
        if (triggered) {
            System.out.printf("[ALERT] %s crossed %s $%.2f — current: $%.2f%n",
                    ticker, direction, threshold, newPrice);
        }
    }
}
