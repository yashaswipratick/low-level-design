# Problem: Cart Service with Pricing Rules
> Domain: Walmart E-commerce | Difficulty: Hard | Est. Time: 45 min

---

## Problem Statement

Design a Cart Service that applies multiple promotions, coupons, and discounts in a defined order of precedence.

Requirements:
1. Cart contains line items (product + quantity)
2. Multiple pricing rules apply: member discount, category promotion, coupon code, bulk discount
3. Rules have a precedence order — they must apply in sequence, not randomly
4. A rule can modify the price and pass to the next rule, or stop the chain
5. Final price breakdown must be itemized (what each rule contributed)
6. Adding a new pricing rule must require zero changes to existing rules

---

## Clarifying Questions to Ask

- Do promotions stack or is it best-deal-wins?
- What is the exact precedence order?
- Can a single item qualify for multiple rules simultaneously?
- Is pricing recalculated on every add-to-cart or only at checkout?
- Is a coupon applied to the whole cart or per-item?
- What if two rules contradict each other?

---

## Key Patterns (Hints)

<details>
<summary>Click to reveal</summary>

- **Chain of Responsibility** — rules applied in defined sequence
- **Strategy** — each rule is an independent pricing strategy
- **Decorator** — layer discounts on top of base price
- **Builder** — construct the `PriceBreakdown` object

</details>

---

## Your Task

1. Identify entities: `Cart`, `CartItem`, `PricingRule`, `PriceBreakdown`
2. Design so adding a "Loyalty Points Discount" rule requires only adding one class
3. Implement in `src/main/java/com/lld/phase8/problems/walmart/cart/`
4. Output must include itemized breakdown of how final price was derived

---

## Edge Cases

- Cart is empty
- Rule applies to only some items in cart (category-specific)
- Coupon reduces price below zero — floor at zero
- Rule is disabled (A/B test off) — should be skippable without removal
