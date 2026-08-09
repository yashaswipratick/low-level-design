package com.lld.phase6.patterns.behavioral.observer.problem3.impl;

import com.lld.phase6.patterns.behavioral.observer.problem3.CartEvent;
import com.lld.phase6.patterns.behavioral.observer.problem3.CartObserver;

import java.util.HashMap;
import java.util.Map;

public class ItemCountUpdater implements CartObserver {
    private Map<String, Integer> itemCountMap = new HashMap<>();

    @Override
    public void onCartChange(CartEvent event, String itemname, double itemPrice) {
        switch (event) {
            case ITEM_CLEARED -> itemCountMap.clear();
            case ITEM_ADDED -> itemCountMap.put(itemname, itemCountMap.getOrDefault(itemname, 0) + 1);
            case ITEM_REMOVED -> {
                if (itemCountMap.containsKey(itemname)) {
                    itemCountMap.compute(itemname, (k, count) -> count - 1);
                }
                if (!itemCountMap.isEmpty() && itemCountMap.get(itemname) <= 0) {
                    itemCountMap.remove(itemname);
                }
            }
        }
        System.out.println("Total item count: " + itemCountMap.size());
    }
}
