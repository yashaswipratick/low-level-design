# Problem: Fulfillment Center Slot Allocation
> Domain: Walmart Supply Chain | Difficulty: Hard | Est. Time: 60 min

---

## Problem Statement

Design a system that assigns incoming orders to fulfillment centers based on multiple factors.

Requirements:
1. Multiple fulfillment centers exist — each has capacity, specialization (cold storage, oversized, standard), and geographic location
2. An order must be assigned to a center that: has capacity, supports the item type, and is within acceptable delivery range
3. Allocation strategy is pluggable: nearest-first, cheapest-first, fastest-delivery-first
4. A center at capacity must be skipped; if no center qualifies, raise an alert
5. Once assigned, a slot is reserved — concurrent orders must not double-allocate
6. Assignment can be reallocated if a center goes offline

---

## Clarifying Questions to Ask

- Can a single order split across multiple fulfillment centers?
- What is the priority: speed, cost, or availability?
- Is reallocation triggered automatically or by an operator?
- How is "delivery range" defined — distance, time, or zone?
- What are the SLAs for allocation (must complete within N ms)?

---

## Key Patterns (Hints)

<details>
<summary>Click to reveal</summary>

- **Strategy** — allocation algorithm is pluggable
- **Factory** — create the right strategy based on order type or SLA
- **Observer** — notify logistics team on allocation or reallocation
- **Chain of Responsibility** — filter centers by capacity → specialization → geo → cost

</details>

---

## Your Task

1. Design `FulfillmentCenter`, `AllocationStrategy`, `OrderAllocationService`
2. Demonstrate swapping from NearestFirstStrategy to CheapestFirstStrategy with zero impact on `OrderAllocationService`
3. Implement in `src/main/java/com/lld/phase8/problems/walmart/fulfillment/`
4. Handle: no center available scenario

---

## Edge Cases

- All centers at capacity
- Center goes offline mid-allocation
- Order contains both cold and standard items
- Two orders racing for the last slot in a center
