package com.lld.phase2.solid.stubs;

/** Stub persistence interface for Orders (used in SRP, DIP problems). */
public interface OrderRepository {
    void save(Order order);
}
