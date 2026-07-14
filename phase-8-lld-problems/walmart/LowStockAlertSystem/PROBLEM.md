# Problem: Low Stock Alert System
> Domain: Walmart E-commerce | Difficulty: Hard | Est. Time: 45–60 min

---

## Problem Statement

Design a Low Stock Alert System for Walmart's e-commerce platform.

When a product's inventory falls below a threshold, the system must:
1. Detect the low stock event in real-time
2. Notify relevant teams (merchandising, warehouse, buyers) via different channels (email, Slack, SMS)
3. Support different alert rules per product category (e.g. electronics threshold = 10, groceries = 50)
4. Avoid duplicate alerts (don't spam if already notified in last 2 hours)
5. Allow adding new notification channels without changing existing code

---

## Clarifying Questions to Ask in Interview

- Is threshold global or per-product/category?
- Can one product have multiple alert rules?
- What channels must be supported? Is it extensible?
- What happens if a channel fails — retry or skip?
- Is deduplication per-product or per-product-per-channel?

---

## Key Patterns (Hints — don't peek until you've tried)

<details>
<summary>Click to reveal</summary>

- **Observer / Event-Driven** — inventory events trigger alerts
- **Strategy** — different notification channel implementations
- **Chain of Responsibility** — alert rules evaluated in sequence
- **Decorator** — add deduplication on top of any notifier

</details>

---

## Your Task

1. Draw the class diagram (on paper or in a `.md` file)
2. Write Java code in `src/main/java/com/lld/phase8/problems/walmart/lowstockalert/`
3. Cover all 5 requirements above
4. Handle edge cases: restock events, channel failures, empty rule chain
5. Submit here — mentor will review

---

## Review Checklist (Mentor will evaluate)

- [ ] Correct pattern identification and application
- [ ] SOLID principles not violated
- [ ] Interfaces used correctly (not concrete classes)
- [ ] Deduplication is transparent (channels don't know about it)
- [ ] New channel can be added with ZERO changes to existing code
- [ ] Edge cases handled (restock, failure, no rules match)
- [ ] Naming is clear and self-documenting
- [ ] No unnecessary coupling
