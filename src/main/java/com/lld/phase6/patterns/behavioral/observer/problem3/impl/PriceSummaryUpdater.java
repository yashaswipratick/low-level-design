package com.lld.phase6.patterns.behavioral.observer.problem3.impl;

import com.lld.phase6.patterns.behavioral.observer.problem3.CartEvent;
import com.lld.phase6.patterns.behavioral.observer.problem3.CartObserver;

public class PriceSummaryUpdater implements CartObserver {
    private double totalPrice = 0.0;

    @Override
    public void onCartChange(CartEvent event, String itemname, double itemPrice) {
        totalPrice = switch (event) {
            case ITEM_ADDED -> totalPrice + itemPrice;
            case ITEM_REMOVED -> totalPrice - itemPrice;
            case ITEM_CLEARED -> 0.0;
        };
        System.out.printf("[Price] Cart total: $%.2f%n", totalPrice);
    }
}
