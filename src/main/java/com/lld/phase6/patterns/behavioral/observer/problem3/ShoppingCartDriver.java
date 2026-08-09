package com.lld.phase6.patterns.behavioral.observer.problem3;

import com.lld.phase6.patterns.behavioral.observer.problem3.impl.ItemCountUpdater;
import com.lld.phase6.patterns.behavioral.observer.problem3.impl.PriceSummaryUpdater;
import com.lld.phase6.patterns.behavioral.observer.problem3.impl.SavingsCalculator;

public class ShoppingCartDriver {

    public static void main(String[] args) {
        ShoppingCart cart = new ShoppingCart();

        cart.subscribe(new PriceSummaryUpdater());
        cart.subscribe(new ItemCountUpdater());
        cart.subscribe(new SavingsCalculator());

        System.out.println("=== Adding items ===");
        cart.addItem("Laptop",  999.99);
        cart.addItem("Mouse",    29.99);
        cart.addItem("Keyboard", 49.99);

        System.out.println("\n=== Removing one item ===");
        cart.removeItem("Mouse");

        System.out.println("\n=== Adding another item ===");
        cart.addItem("Monitor", 299.99);

        System.out.println("\n=== Unsubscribe PriceSummary, then add item ===");
        CartObserver price = new PriceSummaryUpdater();
        cart.subscribe(price);
        cart.addItem("Headphones", 79.99);
        cart.unsubscribe(price);
        cart.addItem("Webcam", 59.99);   // PriceSummary should NOT fire

        System.out.println("\n=== Clear cart ===");
        cart.clearCart();   // all observers reset to 0
    }
}

