# Problem: Swiggy / Food Delivery Platform
> Domain: Marketplace | Difficulty: Hard | Est. Time: 60 min | Interview Frequency: ⭐ Common

---

## Problem Statement

Design the core of a food delivery platform like Swiggy or DoorDash.

Requirements:
1. Customers browse restaurants by location, cuisine, and rating
2. Customer builds a cart from a single restaurant and places an order
3. Order goes through: Placed → Restaurant Accepted → Preparing → Ready → Delivery Assigned → Out for Delivery → Delivered
4. Delivery partner is assigned from available partners near the restaurant
5. Real-time tracking: customer sees live location updates from delivery partner
6. Dynamic delivery fee: based on distance, demand, and weather
7. Estimated delivery time shown at order placement and updated throughout

---

## Clarifying Questions to Ask

- Can an order contain items from multiple restaurants? (Swiggy now allows this — in scope?)
- Is restaurant menu management in scope or just the order flow?
- How is delivery partner assignment done: nearest, fastest, load-balanced?
- Can a customer cancel after restaurant has accepted?
- Is live location updates real-time (WebSocket) or polling?
- Is the rating system in scope (post-delivery rating)?

---

## Key Patterns (Hints)

<details>
<summary>Click to reveal</summary>

- **State** — order lifecycle with valid transitions per actor (customer, restaurant, delivery partner)
- **Observer** — customer, restaurant, and delivery partner all observe order state changes
- **Strategy** — delivery partner assignment algorithm; dynamic pricing strategy
- **Factory** — create different order types (scheduled, ASAP, group order)

</details>

---

## Class Design Starting Point

```
Order
  ├── OrderState currentState
  ├── Restaurant restaurant
  ├── Customer customer
  ├── DeliveryPartner partner (nullable until assigned)
  ├── List<OrderItem> items
  └── void transition(OrderEvent event)

OrderState (interface)
  ├── void onEnter(Order order)
  └── OrderState handle(Order order, OrderEvent event)

DeliveryAssignmentStrategy (interface)
  └── DeliveryPartner assign(Order order, List<DeliveryPartner> available)

PricingStrategy (interface)
  └── Money calculateDeliveryFee(Order order)

OrderObserver (interface)
  └── void onStateChange(Order order, OrderState newState)
```

---

## Your Task

1. Full order state machine with all valid transitions
2. `NearestPartnerStrategy` and `FastestEtaStrategy` for delivery assignment
3. `DynamicDeliveryFeeStrategy` (surge pricing based on demand)
4. Observers: `CustomerNotifier`, `RestaurantNotifier`, `DeliveryPartnerNotifier`
5. `OrderTracker` — exposes real-time position of delivery partner to customer
6. Implement in `src/main/java/com/lld/phase8/problems/advanced/fooddelivery/`

---

## Edge Cases

- Restaurant rejects the order → notify customer, suggest alternatives
- Delivery partner goes offline mid-delivery → reassign
- Customer cancels after food is prepared but before delivery assigned → cancellation policy
- All delivery partners busy → queue order or show extended ETA
- GPS unavailable for delivery partner → fallback tracking
- Order partially ready (some items available, others not) → partial fulfillment?

---

## Extension Points

- Scheduled orders → `ScheduledOrder` with timer-based state trigger
- Group orders → `GroupOrder` composite with multiple customers
- Live tracking widget → Observer publishing GPS coordinates
- Restaurant rating system → post-`Delivered` state triggers rating prompt
