# Problem: Product Review & Rating System
> Domain: Walmart E-commerce | Difficulty: Medium | Est. Time: 45 min

---

## Problem Statement

Design a review and rating system where customers submit reviews that go through moderation before being published, and aggregate ratings update automatically.

Requirements:
1. A customer submits a review (text + star rating 1–5) for a product they purchased
2. Review goes through moderation: profanity check → spam detection → sentiment validation
3. Each moderation check is independent and runs in sequence — one failure rejects the review
4. On publish, the product's aggregate rating is recalculated
5. Seller can respond to a published review
6. A verified purchase review carries more weight in aggregate rating

---

## Clarifying Questions to Ask

- Is moderation automated, manual, or hybrid?
- Can a customer edit a review after publishing?
- What's the weight difference between verified and unverified purchase reviews?
- Can the same customer leave multiple reviews on the same product?
- Are there profanity lists that update dynamically (hot-reload)?

---

## Key Patterns (Hints)

<details>
<summary>Click to reveal</summary>

- **State** — review lifecycle: Submitted → Under Moderation → Published / Rejected
- **Chain of Responsibility** — moderation checks in sequence
- **Observer** — update aggregate rating when a review is published
- **Strategy** — weighted vs unweighted rating aggregation

</details>

---

## Your Task

1. Design the moderation pipeline as an extensible chain
2. The aggregate rating recalculation must trigger automatically — not by explicit call
3. Implement in `src/main/java/com/lld/phase8/problems/walmart/reviews/`
4. Demo: submit review → passes all checks → published → aggregate rating updates

---

## Edge Cases

- First review on a product (aggregate = that review's rating)
- Review rejected at step 2 of 3 — remaining checks must not run
- Customer deletes review — aggregate must recompute
- Product has 10,000 reviews — recalculate on every new review (discuss trade-off)
