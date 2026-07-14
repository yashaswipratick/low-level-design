# Problem: Parking Lot System
> Domain: Infrastructure | Difficulty: Hard | Est. Time: 60 min | Interview Frequency: 🔥 Very Common

---

## Problem Statement

Design a multi-level parking lot system.

Requirements:
1. Multiple floors, each with different slot types: compact, regular, large, motorcycle
2. Different vehicle types: motorcycle, car, truck — each needs a specific slot type or larger
3. Issue a ticket on entry, calculate fee on exit based on time parked
4. Pricing strategy differs: flat rate, hourly rate, daily cap
5. Real-time slot availability per floor and type
6. Support for reserved spots (monthly pass holders)

---

## Clarifying Questions to Ask

- Is it a single building or a campus with multiple lots?
- Can a large vehicle take a compact spot? (usually no)
- Is pricing per vehicle type or per slot type?
- How are monthly pass holders handled at entry?
- Is there a maximum capacity alert needed?
- Is the system for a single lot or a chain (Walmart stores)?

---

## Key Patterns (Hints)

<details>
<summary>Click to reveal</summary>

- **Factory** — create appropriate slot/vehicle type
- **Strategy** — pluggable pricing (flat, hourly, daily cap)
- **Observer** — notify when lot is full or near-full
- **Singleton** — ParkingLot instance (careful — discuss trade-offs)

</details>

---

## Your Task

1. Model all entities: `ParkingLot`, `Floor`, `ParkingSlot`, `Vehicle`, `Ticket`, `PricingStrategy`
2. Implement `park(vehicle)` → issues ticket; `unpark(ticket)` → calculates and returns fee
3. No vehicle parked in wrong slot type
4. Implement in `src/main/java/com/lld/phase8/problems/classics/parkinglot/`

---

## Edge Cases

- Lot is full — reject entry
- Same ticket scanned twice at exit
- Power failure — in-memory state lost (discuss, don't implement)
- Vehicle overstays 24 hours
