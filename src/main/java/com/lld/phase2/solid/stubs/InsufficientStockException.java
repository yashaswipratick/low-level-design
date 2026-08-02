package com.lld.phase2.solid.stubs;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException() { super("Insufficient stock"); }
    public InsufficientStockException(String message) { super(message); }
}
