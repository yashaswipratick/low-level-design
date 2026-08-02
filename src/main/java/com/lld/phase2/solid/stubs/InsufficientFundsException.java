package com.lld.phase2.solid.stubs;

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException() { super("Insufficient funds"); }
    public InsufficientFundsException(String message) { super(message); }
}
