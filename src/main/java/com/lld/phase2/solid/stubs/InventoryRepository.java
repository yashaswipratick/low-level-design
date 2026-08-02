package com.lld.phase2.solid.stubs;

import java.util.Optional;

/** Stub inventory repository — used in DIP problem 2. */
public interface InventoryRepository {
    Optional<Inventory> findByProductId(String productId);
    void save(Inventory inventory);
}
