package com.lld.phase2.solid.stubs;

/** Stub inventory domain object — used in DIP problem 2. */
public class Inventory {
    private final String productId;
    private int available;

    public Inventory(String productId, int available) {
        this.productId = productId;
        this.available = available;
    }

    public void reserve(int quantity) { this.available -= quantity; }
    public void release(int quantity) { this.available += quantity; }

    public String getProductId() { return productId; }
    public int    getAvailable() { return available; }
}
