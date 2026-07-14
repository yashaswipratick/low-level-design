# Problem: Uber — Ride Sharing Platform
> Domain: Marketplace | Difficulty: Hard | Est. Time: 60 min | Interview Frequency: 🔥 Very Common

---

## Problem Statement

Design the core of a ride-sharing platform.

Requirements:
1. Rider requests a ride from location A to B, specifying ride type (economy, premium, XL)
2. System finds nearby available drivers and matches the best one
3. Driver can accept or decline; if declined, next best driver is tried
4. Ride has a lifecycle: Requested → Driver Assigned → Driver En Route → Ride Started → Completed / Cancelled
5. Pricing is dynamic: base fare + per-km + surge multiplier (surge based on demand/supply ratio)
6. Both rider and driver receive real-time updates at each state change

---

## Clarifying Questions to Ask

- Is driver matching purely proximity-based or does rating factor in?
- What is the timeout for driver acceptance before moving to next driver?
- Can a rider cancel after driver is assigned — any penalty?
- How is surge pricing calculated and updated?
- Is the system single-city or multi-city?
- Are scheduled rides in scope?

---

## Key Patterns (Hints)

<details>
<summary>Click to reveal</summary>

- **Strategy** — driver matching algorithm (nearest, highest rated, fastest ETA)
- **State** — ride lifecycle state machine
- **Observer** — notify rider and driver on every state change
- **Factory** — create ride type and pricing model

</details>

---

## Your Task

1. Core entities: `Rider`, `Driver`, `Ride`, `RideState`, `PricingStrategy`, `MatchingStrategy`
2. Ride state machine with valid transitions enforced
3. At least 2 matching strategies, swappable
4. Surge pricing as a strategy modifier
5. Implement in `src/main/java/com/lld/phase8/problems/advanced/uber/`

---

## Edge Cases

- No available drivers in the area
- Driver accepts but then goes offline mid-ride
- Rider and driver both cancel simultaneously
- GPS signal lost — ride distance unknown at completion
- Driver takes detour — how is pricing affected?
