# LLD Problem Bank — 40+ Real-World Problems
> Organized by domain | Difficulty | Key patterns | Interview frequency

---

## How to Approach Every Problem

```
Step 1 → Ask clarifying questions (never skip this — interviewers notice)
Step 2 → Identify core entities and relationships
Step 3 → Name the patterns (say them out loud in interviews)
Step 4 → Draw class diagram (interfaces first)
Step 5 → Implement core flow
Step 6 → Handle edge cases
Step 7 → Discuss trade-offs and extensibility
```

---

## SECTION 1: WALMART / E-COMMERCE PROBLEMS

---

### W1. Low Stock Alert System ⭐ (Must Do)
**Problem:** When inventory falls below threshold, notify relevant teams via multiple channels without spamming.

**Clarifying Questions:**
- Is the threshold global or per-product/category?
- Can one product have multiple alert rules?
- Which channels: email, Slack, SMS? Extensible to new ones?
- What if a channel fails — retry or skip?
- Deduplication: per-product or per-product-per-channel?

**Key Entities:** `Product`, `InventoryEvent`, `AlertRule`, `NotificationChannel`, `AlertDeduplicator`

**Patterns:**
- **Observer** — inventory change triggers alert pipeline
- **Strategy** — each notification channel is a strategy
- **Chain of Responsibility** — alert rules evaluated in sequence
- **Decorator** — deduplication wraps any notifier transparently

**Extension Points:**
- New channel (WhatsApp) → just add a new `NotificationChannel` impl
- New rule (time-of-day based) → add to the chain
- New dedup window → change decorator config

**Code Location:** `phase-8-lld-problems/walmart/LowStockAlertSystem/`

---

### W2. Flash Sale / Deal of the Day Engine ⭐
**Problem:** Time-boxed sales with limited inventory, surge pricing, and eligibility rules (e.g., first-time buyers only).

**Clarifying Questions:**
- Can multiple deals run simultaneously?
- Is inventory pre-reserved or first-come-first-serve?
- Can a user participate in multiple deals in a day?
- What happens when deal inventory hits zero?

**Key Entities:** `Deal`, `DealSlot`, `EligibilityRule`, `PricingStrategy`, `DealState`

**Patterns:**
- **State** — deal lifecycle (Scheduled → Active → Sold Out → Expired)
- **Strategy** — different pricing rules (flat discount, percentage, BOGO)
- **Observer** — notify watchers when deal goes live or sells out
- **Chain of Responsibility** — eligibility checks (login, purchase history, geo)
- **Decorator** — stack eligibility rules dynamically

---

### W3. Cart Service with Pricing Rules
**Problem:** Apply multiple promotions, coupons, and discounts to a cart in a specific order of precedence.

**Clarifying Questions:**
- Can promotions stack or is it best-deal-wins?
- What's the precedence order (coupon > member discount > promo)?
- Can a cart have items from multiple sellers with different rules?
- Is pricing calculated on add-to-cart or at checkout?

**Key Entities:** `Cart`, `CartItem`, `PricingRule`, `Promotion`, `CouponCode`, `PriceBreakdown`

**Patterns:**
- **Chain of Responsibility** — rules applied in sequence (rule 1 → rule 2 → rule 3)
- **Strategy** — each pricing rule is interchangeable
- **Decorator** — stack discounts on top of base price
- **Builder** — construct `PriceBreakdown` object

---

### W4. Order State Machine ⭐
**Problem:** An order moves through states (Placed → Confirmed → Packed → Shipped → Delivered / Cancelled / Returned). Each transition has validation rules and triggers side effects.

**Clarifying Questions:**
- Who can trigger transitions (customer, warehouse, system)?
- What transitions are allowed from each state?
- What side effects happen on each transition (notification, refund, inventory)?
- Is partial cancellation possible?

**Key Entities:** `Order`, `OrderState`, `OrderEvent`, `StateTransition`, `OrderObserver`

**Patterns:**
- **State** — each state handles its own transitions
- **Command** — each transition is a command (cancellable, loggable)
- **Observer** — notify buyer, seller, warehouse on every state change
- **Chain of Responsibility** — validate transition preconditions

---

### W5. Coupon / Promo Code Engine
**Problem:** Validate and apply promo codes with complex rules (min order value, category restrictions, first-time-only, usage limits).

**Clarifying Questions:**
- Can multiple coupons apply to one order?
- Is a coupon tied to a user, SKU, category, or global?
- What's the expiry logic (date-based, usage-count-based, both)?
- What's the fallback if coupon validation service is down?

**Key Entities:** `Coupon`, `ValidationRule`, `DiscountStrategy`, `CouponUsageTracker`

**Patterns:**
- **Strategy** — discount type (flat, percent, free shipping)
- **Chain of Responsibility** — validation rules checked in order
- **Decorator** — combine multiple discount strategies

---

### W6. Price Drop Alert System
**Problem:** Users can watch a product. When price drops below their target, notify them. Avoid spam.

**Clarifying Questions:**
- Is the alert triggered in real-time or batch?
- Multiple users watching same product — one query or N?
- What if price fluctuates up and down repeatedly?
- Notification channels: push, email, SMS?

**Key Entities:** `PriceWatch`, `PriceEvent`, `AlertThreshold`, `NotificationStrategy`

**Patterns:**
- **Observer** — product is observable, users are observers
- **Strategy** — notification channel per user preference
- **Decorator** — deduplication / rate limiting on notifications

---

### W7. Return & Refund Workflow
**Problem:** A return request goes through inspection, approval, and refund processing with different rules per product category.

**Clarifying Questions:**
- Is return window per-product or category-level?
- Does return require physical product pickup?
- Refund to original payment method or store credit?
- Who approves: automated or human review?

**Key Entities:** `ReturnRequest`, `ReturnPolicy`, `RefundStrategy`, `ReturnState`, `InspectionResult`

**Patterns:**
- **State** — return lifecycle (Requested → Under Review → Approved → Refunded / Rejected)
- **Strategy** — refund method (original payment, wallet, store credit)
- **Chain of Responsibility** — return eligibility checks
- **Command** — refund command (reversible, auditable)

---

### W8. Fulfillment Center Slot Allocation
**Problem:** Assign orders to fulfillment centers based on proximity, capacity, and specialty (cold storage, oversized items).

**Clarifying Questions:**
- Can an order split across multiple centers?
- What's the priority: speed vs cost vs availability?
- What if the optimal center is at capacity?
- Is reallocation possible after assignment?

**Key Entities:** `Order`, `FulfillmentCenter`, `AllocationStrategy`, `CapacityRule`, `SlotReservation`

**Patterns:**
- **Strategy** — allocation algorithm (nearest, cheapest, fastest)
- **Factory** — create appropriate strategy based on order type
- **Observer** — notify logistics on assignment

---

### W9. Product Review & Rating System
**Problem:** Customers leave reviews, which go through moderation before publishing. Aggregate ratings update in real-time.

**Clarifying Questions:**
- Is moderation automated (NLP) or manual?
- Can seller respond to reviews?
- Is the aggregate rating weighted (verified purchases count more)?
- Can the same user leave multiple reviews?

**Key Entities:** `Review`, `ModerationRule`, `RatingAggregator`, `ReviewState`

**Patterns:**
- **State** — review lifecycle (Submitted → Moderation → Published / Rejected)
- **Chain of Responsibility** — moderation rules (profanity, spam, sentiment)
- **Observer** — update aggregate rating on publish
- **Strategy** — rating aggregation algorithm

---

### W10. Search Autocomplete / Typeahead
**Problem:** As user types, suggest completions ranked by relevance and personalization.

**Clarifying Questions:**
- Is ranking personalized per user or global?
- What's the latency budget?
- Handle typos / fuzzy match?
- How are suggestions refreshed when catalog changes?

**Key Entities:** `SearchQuery`, `SuggestionProvider`, `RankingStrategy`, `SearchIndex`

**Patterns:**
- **Strategy** — ranking algorithm (frequency, personalized, trending)
- **Decorator** — add typo tolerance on top of base provider
- **Factory** — create appropriate provider based on query type

---

## SECTION 2: CLASSIC LLD PROBLEMS (Interview Staples)

---

### C1. Parking Lot ⭐ (Most Asked)
**Concepts:** Factory, Strategy, Observer, Singleton
- Multi-level parking, different vehicle types, different pricing strategies
- Real-time slot availability, entry/exit management

### C2. BookMyShow / Movie Ticket Booking ⭐
**Concepts:** Factory, Strategy, Observer, State, Singleton
- Show scheduling, seat selection, payment, concurrency (two users same seat)

### C3. Chess Game ⭐
**Concepts:** Factory, Command, State, Strategy
- Piece movement rules, check/checkmate detection, undo/redo moves

### C4. Snake and Ladder
**Concepts:** Observer, Strategy, Template Method
- Board setup, player turns, win condition, extensible to new board games

### C5. Elevator System ⭐
**Concepts:** State, Strategy, Observer
- Multiple elevators, request dispatching algorithm, floor scheduling

### C6. ATM Machine ⭐
**Concepts:** State, Command, Strategy, Chain of Responsibility
- Card validation, PIN check, withdrawal, insufficient funds, maintenance mode

### C7. Library Management System
**Concepts:** Factory, Observer, Strategy
- Book catalog, member management, borrow/return, late fees, reservations

### C8. Hotel Booking System
**Concepts:** Strategy, Observer, State, Factory
- Room types, availability calendar, pricing by season, cancellation policy

### C9. Splitwise / Expense Sharing ⭐
**Concepts:** Strategy, Observer
- Multiple split strategies (equal, exact, percentage, shares)
- Debt simplification algorithm

### C10. Vending Machine ⭐
**Concepts:** State, Strategy
- Coin insertion, item selection, change dispensing, out-of-stock handling

---

## SECTION 3: PLATFORM & SYSTEM PROBLEMS

---

### P1. Rate Limiter ⭐
**Concepts:** Strategy, Decorator, Chain of Responsibility
- Token Bucket, Leaky Bucket, Sliding Window Log, Fixed Window
- Per-user, per-IP, per-endpoint limiting

### P2. Logger Framework (like Log4j / SLF4J)
**Concepts:** Singleton, Chain of Responsibility, Strategy, Decorator
- Log levels, multiple appenders, filtering, formatting

### P3. LRU Cache ⭐
**Concepts:** Strategy, Decorator
- Eviction policies (LRU, LFU, FIFO) as strategies
- Thread-safe implementation

### P4. Notification System ⭐
**Concepts:** Observer, Strategy, Chain of Responsibility, Decorator
- Multiple channels, preference management, deduplication, retry

### P5. Payment Gateway
**Concepts:** Strategy, Chain of Responsibility, Decorator
- Multiple payment providers, retry logic, fraud detection, idempotency

### P6. File System
**Concepts:** Composite, Iterator, Visitor
- Files and directories, recursive operations, search, permissions

### P7. Uber / Ride Sharing ⭐
**Concepts:** Strategy, Observer, State, Factory
- Driver matching, surge pricing, trip state machine, notifications

### P8. Swiggy / Food Delivery ⭐
**Concepts:** Observer, Strategy, State, Factory
- Restaurant catalog, order flow, delivery assignment, real-time tracking

### P9. LinkedIn / Social Network
**Concepts:** Observer, Strategy, Composite
- Connections, feed generation, notifications, search

### P10. Messaging App (WhatsApp-style)
**Concepts:** Observer, Strategy, State
- Send/receive, delivery receipts, group chats, online/offline status

### P11. Traffic Signal Controller
**Concepts:** State, Observer, Strategy
- Signal cycles, emergency override, pedestrian crossing, sensor integration

### P12. Coffee Machine
**Concepts:** State, Builder, Strategy
- Ingredient management, recipe building, error states

### P13. Inventory Management
**Concepts:** Observer, Strategy, Factory
- Stock tracking, reorder triggers, supplier management, audit trail

### P14. Online Shopping Platform (Amazon-style)
**Concepts:** Factory, Strategy, Observer, State
- Product catalog, cart, checkout, order management, seller integration

### P15. Bank / Banking System
**Concepts:** State, Command, Strategy, Chain of Responsibility
- Accounts, transactions, transfers, fraud detection, audit logging

---

## SECTION 4: ADVANCED / STAFF ENGINEER LEVEL

---

### A1. Distributed Rate Limiter
Token bucket with Redis backend, consistency guarantees

### A2. Workflow Engine
DAG-based task execution, retry policies, compensating transactions

### A3. Event Sourcing System
Append-only event log, projections, snapshots, replay

### A4. Rule Engine
Dynamic business rules, expression evaluation, hot reload

### A5. Feature Flag System
Gradual rollout, A/B testing, kill switch, per-user targeting

---

## Problem Difficulty Summary

| Level | Count | Examples |
|-------|-------|---------|
| Easy | 5 | Coffee Machine, Snake & Ladder, Traffic Signal |
| Medium | 12 | Library, Hotel, Product Review, Snake & Ladder |
| Hard | 23 | Parking Lot, Uber, BookMyShow, Low Stock Alert |
| Staff | 5 | Rule Engine, Workflow Engine, Feature Flags |

---

## Interview Frequency by Problem

| 🔥 Very Common | ⭐ Common | 📌 Occasionally |
|----------------|----------|----------------|
| Parking Lot | Low Stock Alert | Traffic Signal |
| BookMyShow | Flash Sale Engine | Workflow Engine |
| LRU Cache | Order State Machine | Feature Flags |
| Rate Limiter | Notification System | Messaging App |
| Elevator | Splitwise | LinkedIn |
| ATM | Vending Machine | Coffee Machine |
| Chess | Payment Gateway | File System |
| Uber | Cart with Pricing | Logger |
