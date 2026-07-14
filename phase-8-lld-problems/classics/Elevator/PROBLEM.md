# Problem: Elevator System
> Domain: Infrastructure | Difficulty: Hard | Est. Time: 60 min | Interview Frequency: ⭐ Common

---

## Problem Statement

Design a multi-elevator system for a building.

Requirements:
1. Building has N floors and M elevators
2. Users press a button (up/down) on a floor, or select a floor inside the elevator
3. Dispatcher assigns the best elevator to each request
4. Dispatching algorithm is pluggable: nearest elevator, least loaded, directional (SCAN/LOOK)
5. Each elevator has states: Idle, Moving Up, Moving Down, Maintenance
6. Elevator notifies when it arrives at a floor

---

## Clarifying Questions to Ask

- Are all elevators identical or do some serve only certain floors (express elevator)?
- Is there a VIP/emergency elevator that overrides normal dispatch?
- What happens when an elevator is in Maintenance — does it reject requests?
- How are simultaneous requests from multiple floors handled?
- Is there a maximum weight limit that affects dispatch?

---

## Key Patterns (Hints)

<details>
<summary>Click to reveal</summary>

- **State** — elevator states: Idle, MovingUp, MovingDown, Maintenance
- **Strategy** — dispatching algorithm (nearest, SCAN/LOOK)
- **Observer** — notify waiting users when elevator arrives

</details>

---

## Your Task

1. Model: `Elevator`, `ElevatorController`, `DispatchStrategy`, `FloorRequest`, `ElevatorState`
2. Implement at least 2 dispatching strategies and show them swappable
3. An elevator in Maintenance state must not accept requests
4. Implement in `src/main/java/com/lld/phase8/problems/classics/elevator/`

---

## Edge Cases

- All elevators are moving away from requested floor
- Elevator at max capacity — should it skip floor?
- Power failure — all elevators go to ground floor
- Emergency — all elevators go to ground and lock
