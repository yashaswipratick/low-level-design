package com.lld.phase6.patterns.behavioral.observer.problem2;

public interface StockObserver {

    void onPriceChange(String stockSymbol, double oldPrice, double newPrice);
}
