package com.lld.phase6.patterns.behavioral.observer.problem3;

public interface CartObserver {

    void onCartChange(CartEvent event, String itemname, double itemPrice);
}
