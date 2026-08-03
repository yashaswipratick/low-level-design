package com.lld.phase6.patterns.behavioral.observer.problem2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StockMarket {

    private Map<String, Double> stockPrices= new HashMap<>();
    private List<StockObserver>  observers = new ArrayList<>();

    public void subscribe(StockObserver observer){
        observers.add(observer);
    }

    public void unsubscribe(StockObserver observer){
        observers.remove(observer);
    }

    private void notifyPriceChange(String stockSymbol, double oldPrice, double newPrice) {
        for (StockObserver observer : observers) {
            observer.onPriceChange(stockSymbol, oldPrice, newPrice);
        }
    }

    public void updateStockPrice(String stockSymbol, double newPrice) {
        double oldPrice = stockPrices.getOrDefault(stockSymbol, newPrice);
        stockPrices.put(stockSymbol, newPrice);
        if (oldPrice != newPrice) {
            observers.forEach(o -> {
                o.onPriceChange(stockSymbol, oldPrice, newPrice);
                notifyPriceChange(stockSymbol, oldPrice, newPrice);
            });
        }
    }
}
