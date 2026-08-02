package com.lld.phase2.solid.stubs;

/** Stub — warehouse notification client (used in DIP problem 2). */
public interface WarehouseClient {
    void notifyReservation(String productId, int quantity);
}
