# Problem: Return & Refund Workflow
> Domain: Walmart E-commerce | Difficulty: Hard | Est. Time: 60 min

---

## Problem Statement

Design a Return & Refund system where customers initiate returns, which go through inspection and approval, and result in a refund via the original or alternate payment method.

Requirements:
1. Customer initiates a return with a reason (damaged, wrong item, changed mind)
2. Return eligibility checked against category-specific policy (electronics: 7 days, apparel: 30 days, groceries: not returnable)
3. Return request moves through states: Requested → Under Review → Approved → Pickup Scheduled → Inspected → Refunded / Rejected
4. Refund method can differ from original payment (e.g. paid by card → refund to wallet or card, customer's choice)
5. Each state transition triggers side effects (customer notification, warehouse update, finance system trigger)
6. Audit trail for every transition

---

## Clarifying Questions to Ask

- Is return window calculated from delivery date or purchase date?
- Does every return need physical pickup or can customer self-ship?
- Can partial returns be initiated (some items from an order)?
- Who approves the return — automated system or human agent?
- What if inspection reveals product was not in returnable condition?
- Is refund immediate on approval or after physical inspection?

---

## Key Patterns (Hints)

<details>
<summary>Click to reveal</summary>

- **State** — return request lifecycle
- **Strategy** — refund method (original card, wallet, store credit)
- **Chain of Responsibility** — eligibility validation (category policy, time window, condition)
- **Command** — each transition as a reversible, auditable command
- **Observer** — notify buyer and warehouse on state change

</details>

---

## Your Task

1. Design the full state machine for a return request
2. Enforce that eligibility must pass before a request is created
3. Implement in `src/main/java/com/lld/phase8/problems/walmart/returns/`
4. Demo: apparel return within 30 days → approved → refunded to wallet

---

## Edge Cases

- Return initiated after policy window — must be rejected clearly
- Item lost in transit during return — who bears cost?
- Customer initiates two returns for same item
- Refund fails (payment gateway down) — return already approved
