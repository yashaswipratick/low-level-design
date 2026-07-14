# Problem: Vending Machine
> Domain: Embedded/Retail | Difficulty: Medium | Est. Time: 45 min | Interview Frequency: ⭐ Common

---

## Problem Statement

Design a vending machine that accepts coins/cash, allows item selection, and dispenses items with change.

Requirements:
1. Machine has slots — each slot holds a specific item with a price and quantity
2. User inserts money (coins or notes), selects an item
3. Machine validates: sufficient money inserted, item in stock
4. Dispenses item and returns change (using minimum coins)
5. Machine has states: Idle, MoneyInserted, Dispensing, OutOfStock, Maintenance
6. Admin can restock items and collect cash

---

## Clarifying Questions to Ask

- Does the machine support card payments or only cash?
- Can a user select multiple items in one session?
- What happens if the machine can't give exact change?
- Is the machine networked (reports to central system) or standalone?
- Can items be different sizes (takes up multiple slots)?

---

## Key Patterns (Hints)

<details>
<summary>Click to reveal</summary>

- **State** — machine states drive all behavior
- **Strategy** — change-dispensing algorithm (greedy, exact match)
- **Command** — each user action is a command

</details>

---

## Your Task

1. Model: `VendingMachine`, `Slot`, `Item`, `VendingState`, `Coin`
2. All behavior MUST be driven by state — no `if (state == X)` checks outside state classes
3. User inserts ₹20, item costs ₹15 → dispense item + return ₹5 change
4. Implement in `src/main/java/com/lld/phase8/problems/classics/vendingmachine/`

---

## Edge Cases

- User inserts money then cancels — full refund
- Item selected but machine can't give change — reject or proceed?
- Item falls but doesn't dispense (mechanical failure)
- Admin tries to restock while machine is serving a customer
