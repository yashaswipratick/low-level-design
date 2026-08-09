package com.lld.phase6.patterns.behavioral.observer.problem3.impl;

import com.lld.phase6.patterns.behavioral.observer.problem3.CartEvent;
import com.lld.phase6.patterns.behavioral.observer.problem3.CartObserver;

import java.util.HashMap;
import java.util.Map;

public class SavingsCalculator implements CartObserver {
    private Map<String, Double> itemPriceMap= new HashMap<>();
    private int discount = 20;

    @Override
    public void onCartChange(CartEvent event, String itemname, double itemPrice) {
        switch (event) {
            case ITEM_ADDED   -> itemPriceMap.put(itemname, itemPrice);
            case ITEM_REMOVED -> itemPriceMap.remove(itemname);
            case ITEM_CLEARED -> itemPriceMap.clear();
        }
        double sum = itemPriceMap.values().stream().map(aDouble -> (aDouble * discount) / 100)
                .toList()
                .stream().mapToDouble(i -> i).sum();
        System.out.println("Total discount : " + sum);
    }
}
