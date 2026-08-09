package com.lld.phase6.patterns.behavioral.observer.problem3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShoppingCart {

    private List<CartObserver> observers = new ArrayList<>();
    private Map<String, Double> items = new HashMap<>();

    public void subscribe(CartObserver observer) {
        observers.add(observer);
    }

    public void unsubscribe(CartObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers(CartEvent event, String itemName, double price) {
        for (CartObserver observer : observers) {
            observer.onCartChange(event, itemName, price);
        }
    }

    public void addItem(String itemName, double price) {
        items.put(itemName, price);
        notifyObservers(CartEvent.ITEM_ADDED, itemName, price);
    }

    public void removeItem(String itemName) {
        if (items.containsKey(itemName)) {
            double price = items.get(itemName);
            items.remove(itemName);
            notifyObservers(CartEvent.ITEM_REMOVED, itemName, price);
        }
    }

    public void clearCart() {
        items.clear();
        notifyObservers(CartEvent.ITEM_CLEARED, null, 0);
    }
}
