package com.lld.phase6.patterns.behavioral.observer.problem2;

import com.lld.phase6.patterns.behavioral.observer.problem2.impl.PortfolioTracker;
import com.lld.phase6.patterns.behavioral.observer.problem2.impl.PriceAlertNotifier;

import java.util.Map;

public class StockMarketDriver {

    public static void main(String[] args) {
        StockMarket market = new StockMarket();

        market.subscribe(new PortfolioTracker(Map.of("AAPL", 10.0, "GOOGL", 5.0)));
        market.subscribe(new PriceAlertNotifier("AAPL", 155.0, PriceDirection.ABOVE));

        market.updateStockPrice("AAPL", 150.0);
        market.updateStockPrice("GOOGL", 2800.0);
        market.updateStockPrice("AAPL", 158.0);
    }
}
