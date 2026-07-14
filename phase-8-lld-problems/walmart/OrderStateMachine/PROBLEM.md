# Problem: Order State Machine
> Domain: Walmart E-commerce | Difficulty: Hard | Est. Time: 60 min

---

## Problem Statement

Design an Order management system where an order moves through well-defined states with validation and side effects on every transition.

States:
```
PLACED → PAYMENT_CONFIRMED → PACKED → SHIPPED → OUT_FOR_DELIVERY → DELIVERED
                                                                  ↘ FAILED
PLACED → CANCELLED
DELIVERED → RETURN_REQUESTED → RETURN_APPROVED → REFUNDED
```

Requirements:
1. Each state transition must be validated (e.g. cannot ship before packing)
2. Illegal transitions must throw a meaningful exception
3. Every transition triggers side effects: notify buyer, update warehouse, trigger refund
4. Support undo for certain transitions (e.g. cancel a just-placed order)
5. All state changes must be auditable (who changed what and when)

---

## Clarifying Questions to Ask

- Who can trigger each transition (customer, warehouse, system, admin)?
- Is partial cancellation supported (some items from an order)?
- On cancellation post-shipment — is return flow triggered automatically?
- Are notifications synchronous or async?
- How is audit log stored — in-memory for LLD or described architecturally?

---

## Key Patterns (Hints)

<details>
<summary>Click to reveal</summary>

- **State** — each order state handles its own valid transitions
- **Command** — each transition is a command (auditable, potentially reversible)
- **Observer** — side effects (notification, warehouse update) triggered on transition
- **Chain of Responsibility** — validate preconditions before applying transition

</details>

---

## Your Task

1. Model all states as classes/enums
2. Enforce illegal transition prevention at the type level if possible
3. Implement in `src/main/java/com/lld/phase8/problems/walmart/order/`
4. Write a demo showing: Placed → Confirmed → Packed → Shipped → Delivered
5. Then show: Placed → Cancelled (with refund side effect)

---

## Edge Cases

- Transition triggered twice (idempotency)
- Network failure during side effect — state already changed but notification failed
- Admin force-transitions an order (bypass normal rules)
- Order in SHIPPED state — customer tries to cancel
