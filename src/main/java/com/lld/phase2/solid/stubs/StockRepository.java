package com.lld.phase2.solid.stubs;

/** Stub — checks available stock for a product. */
public interface StockRepository {
    int getAvailable(String productId);
}
