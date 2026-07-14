# Problem: Coupon / Promo Code Engine
> Domain: Walmart E-commerce | Difficulty: Hard | Est. Time: 45 min

---

## Problem Statement

Design a Coupon engine that validates and applies promo codes with complex, composable rules.

Requirements:
1. A coupon has a code, discount type (flat/percent/free-shipping), and a set of validation rules
2. Validation rules: minimum cart value, category restriction, first-time-buyer-only, usage limit, expiry date
3. A coupon is invalid if ANY rule fails
4. Multiple coupons can be applied — but only if they don't conflict
5. Usage tracking — a coupon used 100 times out of 100-use limit must be rejected on the 101st
6. Adding a new validation rule must require zero changes to existing rules or coupon code

---

## Clarifying Questions to Ask

- Can multiple coupons be applied to one cart simultaneously?
- Is the usage limit global or per-user?
- Who creates coupons — merchants, Walmart centrally, or both?
- Is validation real-time or cached?
- What's the rollback strategy if coupon is applied but payment fails?

---

## Key Patterns (Hints)

<details>
<summary>Click to reveal</summary>

- **Strategy** — discount type (flat, percent, free shipping) as interchangeable strategies
- **Chain of Responsibility** — validation rules checked in sequence, fail-fast
- **Decorator** — stack multiple validators on a coupon

</details>

---

## Your Task

1. Design `Coupon`, `ValidationRule`, `DiscountStrategy`, `CouponValidator`
2. The system must say WHY a coupon failed (which rule), not just "invalid"
3. Implement in `src/main/java/com/lld/phase8/problems/walmart/coupon/`
4. Demo: valid coupon applied, expired coupon rejected, usage-limit exceeded rejected

---

## Edge Cases

- Coupon code is case-insensitive
- Coupon valid for category A applied to a cart with only category B items
- Two users attempt to use the last available use of a coupon simultaneously
- Coupon applied, then item removed from cart — recalculate or remove coupon?
