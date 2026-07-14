# Problem: Flash Sale / Deal of the Day Engine
> Domain: Walmart E-commerce | Difficulty: Hard | Est. Time: 60 min

---

## Problem Statement

Design a Flash Sale engine for Walmart's platform where time-boxed deals run with limited inventory, special pricing, and eligibility rules.

Requirements:
1. A deal has a start time, end time, discounted price, and limited quantity
2. A user must pass eligibility checks before purchasing at deal price (e.g. first-time buyer, account age > 30 days, geo-restricted)
3. Multiple pricing strategies: flat discount, percentage off, Buy-One-Get-One
4. Deal moves through states: Scheduled → Active → Sold Out → Expired
5. When a deal goes live or sells out, notify subscribed users
6. Eligibility rules must be composable — new rules added without changing core logic

---

## Clarifying Questions to Ask

- Can multiple deals run simultaneously on the same product?
- Is inventory pre-reserved per user or first-come-first-serve?
- Can a user participate in multiple deals in one day?
- What happens to users waiting when deal hits Sold Out?
- Is eligibility evaluated client-side or server-side?
- Do notifications go to all platform users or only those who opted in?

---

## Key Patterns (Hints)

<details>
<summary>Click to reveal</summary>

- **State** — deal lifecycle management
- **Strategy** — pricing algorithm (flat, percent, BOGO)
- **Chain of Responsibility** — eligibility rules checked in sequence
- **Observer** — notify users on deal state transitions
- **Decorator** — stack eligibility rules dynamically

</details>

---

## Your Task

1. Identify all entities and their relationships
2. Draw class diagram
3. Implement in `src/main/java/com/lld/phase8/problems/walmart/flashsale/`
4. Demonstrate: deal goes live → user tries to purchase → eligibility check → pricing applied → inventory decremented → deal sold out → users notified

---

## Edge Cases to Handle

- Two users purchasing last item simultaneously (concurrency)
- Deal scheduled but product goes out of stock before start
- User passes eligibility but payment fails — should inventory be held?
- Deal expired mid-purchase
