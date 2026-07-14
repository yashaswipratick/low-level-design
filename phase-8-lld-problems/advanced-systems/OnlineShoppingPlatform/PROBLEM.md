# Problem: Online Shopping Platform (Amazon / Flipkart-style)
> Domain: E-Commerce | Difficulty: Hard | Est. Time: 75 min | Interview Frequency: ⭐ Common

---

## Problem Statement

Design the core of an e-commerce platform.

Requirements:
1. Product catalog with categories, attributes, and variants (size, color)
2. Shopping cart: add/remove items, apply coupons, calculate total
3. Checkout: address selection, payment, order placement
4. Order lifecycle: Placed → Confirmed → Shipped → Delivered / Cancelled / Returned
5. Seller management: multiple sellers can list the same product at different prices
6. Search and filtering: by category, price range, rating, brand
7. Recommendations: "customers also bought" (design the interface, not the ML model)

---

## Clarifying Questions to Ask

- Is this a marketplace (multiple sellers) or single-seller platform?
- Are product variants separate SKUs or variant groups?
- Is the search engine in-house or via Elasticsearch integration?
- Can an order contain items from multiple sellers?
- What are the cancellation window rules (before/after shipment)?
- Is flash sale / time-limited pricing in scope?

---

## Key Patterns (Hints)

<details>
<summary>Click to reveal</summary>

- **Factory** — create appropriate `ProductListing` based on product category; create `Order` based on seller count
- **Strategy** — pricing strategy (base price, promo, B2B bulk); search ranking algorithm
- **Observer** — order state change triggers inventory update, seller notification, buyer email
- **State** — order state machine
- **Decorator** — `PriceDecorator` stacks discounts (member discount + coupon + sale)
- **Composite** — `Category` tree (Electronics → Mobiles → Smartphones)

</details>

---

## Class Design Starting Point

```
Product
  ├── String productId
  ├── String name
  ├── Category category
  ├── List<ProductListing> listings   // one per seller

ProductListing
  ├── Seller seller
  ├── Money price
  ├── int stockAvailable
  └── List<ProductAttribute> attributes

Cart
  ├── String customerId
  ├── List<CartItem> items
  └── Money calculateTotal(List<PricingRule> rules)

Order
  ├── List<OrderItem> items
  ├── OrderState state
  ├── Address deliveryAddress
  └── PaymentInfo payment

SearchService
  └── List<ProductListing> search(SearchQuery query, RankingStrategy ranking)

Category (Composite)
  ├── String name
  ├── List<Category> subcategories
  └── List<Product> products
```

---

## Your Task

1. Product catalog with Category tree (Composite)
2. Cart with `PricingRule` chain (Chain of Responsibility)
3. Order state machine with valid transitions
4. `SellerNotificationObserver` and `InventoryUpdateObserver` on order events
5. `SearchService` with pluggable `RankingStrategy`
6. Implement in `src/main/java/com/lld/phase8/problems/advanced/shopping/`

---

## Edge Cases

- Item added to cart goes out of stock before checkout — validation at checkout
- Two customers checkout same last unit simultaneously — inventory race
- Seller deactivates listing mid-checkout — fail or honor existing cart price?
- Return after 30 days when policy is 30-day window — boundary handling
- Cart with items from 3 different sellers, one seller cancels their item — partial order
- Price changes between add-to-cart and checkout — honor cart price or current price?

---

## Extension Points

- Wishlist → `WishList` with Observer to notify when price drops
- Product bundling → `BundleProduct` as Composite of individual products
- Flash sale → `FlashSaleDecorator` on `ProductListing` with countdown timer
- Loyalty points → `LoyaltyDecorator` on `Cart` to accrue/redeem points
