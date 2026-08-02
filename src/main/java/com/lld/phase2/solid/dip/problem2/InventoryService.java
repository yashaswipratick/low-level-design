package com.lld.phase2.solid.dip.problem2;

import com.lld.phase2.solid.stubs.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// TODO: DIP VIOLATION — field injection hides dependencies and makes this class untestable
// without a full Spring context.
//
// 3 concrete reasons @Autowired field injection is bad in production:
//   1. HIDDEN dependencies — you can't see what a class needs without reading its body
//   2. NOT testable without Spring — `new InventoryService()` leaves all fields null → NPE
//   3. NOT immutable — fields can be reassigned; partial construction is possible
//   (Also: circular dependency detection is harder; fields are not `final`)
//
// Your task:
//   1. Replace field injection with constructor injection
//   2. Make all three fields `final`
//   3. Write InventoryServiceTest that uses `new InventoryService(mock, mock, mock)` — no Spring

@Service
public class InventoryService {

    // VIOLATION: field injection — hidden, mutable, untestable without Spring context
    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private WarehouseClient warehouseClient;

    @Autowired
    private AuditLogger auditLogger;

    public void reserveStock(String productId, int quantity) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
            .orElseThrow(() -> new ProductNotFoundException(productId));

        if (inventory.getAvailable() < quantity) {
            throw new InsufficientStockException(
                "Requested: " + quantity + ", Available: " + inventory.getAvailable()
            );
        }

        inventory.reserve(quantity);
        inventoryRepository.save(inventory);

        warehouseClient.notifyReservation(productId, quantity);
        auditLogger.log("STOCK_RESERVED", productId, String.valueOf(quantity));
    }

    public void releaseStock(String productId, int quantity) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
            .orElseThrow(() -> new ProductNotFoundException(productId));

        inventory.release(quantity);
        inventoryRepository.save(inventory);

        auditLogger.log("STOCK_RELEASED", productId, String.valueOf(quantity));
    }
}
