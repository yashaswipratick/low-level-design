# Problem: Price Drop Alert System
> Domain: Walmart E-commerce | Difficulty: Medium-Hard | Est. Time: 45 min

---

## Problem Statement

Design a Price Drop Alert system where users can watch a product and be notified when the price drops below their target price.

Requirements:
1. A user can watch any product with an optional target price (or default = any drop)
2. When a product's price changes, check all watchers and notify eligible ones
3. Support multiple notification channels per user (email, push, SMS) based on user preference
4. Avoid spam — don't notify a user for the same product more than once per 24 hours
5. User can unwatch a product at any time

---

## Clarifying Questions to Ask

- Is price change real-time (event-driven) or polled periodically?
- Can the same user watch the same product multiple times with different target prices?
- What if price drops and then rises again within minutes — should the first drop notify?
- Is notification preference per-product or per-user globally?
- What's the scale — thousands of products and millions of watchers?

---

## Key Patterns (Hints)

<details>
<summary>Click to reveal</summary>

- **Observer** — product is the observable, users are observers
- **Strategy** — notification channel per user preference
- **Decorator** — rate limiting / deduplication on notifications

</details>

---

## Your Task

1. Model `PriceWatch`, `Product`, `PriceChangeEvent`, `WatcherNotifier`
2. When a price drops, only notify watchers whose target price threshold is met
3. Implement in `src/main/java/com/lld/phase8/problems/walmart/pricedrop/`
4. Demo: user watches TV at target ₹50,000 — price drops to ₹48,000 → notified. Drops again to ₹47,000 within 24h → NOT notified (dedup).

---

## Edge Cases

- Product is discontinued — remove all watches
- User deletes account — clean up all their watches
- Price drops exactly to target (inclusive vs exclusive comparison)
- Multiple users watching same product — batch notify or individual?
