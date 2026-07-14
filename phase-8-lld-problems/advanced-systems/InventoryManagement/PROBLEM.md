# Problem: Inventory Management System
> Domain: Supply Chain | Difficulty: Medium | Est. Time: 45 min | Interview Frequency: 📌 Occasional

---

## Problem Statement

Design an Inventory Management System for a warehouse.

Requirements:
1. Track stock levels for SKUs (Stock Keeping Units) across multiple warehouses
2. Stock can be reserved (soft lock), then confirmed (hard deduct) or released
3. Reorder triggers: when stock falls below threshold, trigger a purchase order
4. Audit trail: every stock movement (receipt, sale, adjustment, transfer) is logged
5. Supplier management: multiple suppliers per SKU with lead times and minimum order quantities
6. Transfer stock between warehouses
7. Support multiple units of measure (each, kg, litre) with conversion

---

## Clarifying Questions to Ask

- Is stock tracked at warehouse level or bin/shelf level within warehouse?
- Is reservation per-order or per-customer?
- Is reorder triggered automatically or just raises an alert?
- Are multi-warehouse transfers atomic (both sides update together)?
- Is the system real-time or batch-updated?
- Are returns (stock coming back) handled?

---

## Key Patterns (Hints)

<details>
<summary>Click to reveal</summary>

- **Observer** — stock level changes trigger reorder alerts, low-stock notifications
- **Strategy** — reorder strategy (fixed quantity, economic order quantity, just-in-time)
- **Factory** — create `StockMovement` based on movement type (receipt, sale, transfer, adjustment)
- **Command** — each stock movement is a command (auditable, reversible for corrections)

</details>

---

## Class Design Starting Point

```
InventoryItem
  ├── SKU sku
  ├── Warehouse warehouse
  ├── int quantityOnHand
  ├── int quantityReserved
  └── int quantityAvailable()  // onHand - reserved

StockMovement (Command)
  ├── MovementType type         // RECEIPT, SALE, TRANSFER_OUT, TRANSFER_IN, ADJUSTMENT
  ├── int quantity
  ├── Instant timestamp
  └── String reference          // order ID, PO number, etc.

ReorderRule
  ├── SKU sku
  ├── int reorderPoint
  ├── ReorderStrategy strategy

ReorderStrategy (interface)
  └── int calculateReorderQuantity(InventoryItem item)

InventoryObserver (interface)
  └── void onStockChange(SKU sku, Warehouse warehouse, int newLevel)
```

---

## Your Task

1. `InventoryService` with reserve, confirm, and release operations
2. `StockMovementLog` — append-only audit log of all movements
3. `LowStockReorderObserver` — triggers reorder when quantity drops below reorder point
4. `FixedQuantityReorderStrategy` and `EconomicOrderQuantityStrategy`
5. `WarehouseTransfer` — atomic transfer between two warehouses
6. Implement in `src/main/java/com/lld/phase8/problems/advanced/inventory/`

---

## Edge Cases

- Reserve more than available — fail or queue?
- Transfer request when source warehouse has insufficient stock
- Multiple orders reserving the same last unit simultaneously — concurrency
- Stock count correction (physical audit shows discrepancy) — negative adjustments
- SKU discontinued but has remaining stock — prevent new receipts
- Reorder triggered but supplier has infinite lead time

---

## Extension Points

- Batch/lot tracking → `LotNumber` attribute on `InventoryItem`
- Expiry date tracking → `ExpiryDateObserver` alerts on near-expiry items
- Multi-location picking → `PickingStrategy` selects which warehouse to fulfill from
- Supplier portal integration → `PurchaseOrder` raised automatically on reorder trigger
