# Problem: BookMyShow — Movie Ticket Booking
> Domain: Entertainment Platform | Difficulty: Hard | Est. Time: 60 min | Interview Frequency: 🔥 Very Common

---

## Problem Statement

Design an online movie ticket booking system.

Requirements:
1. Movies are shown in multiple theatres, each with multiple screens and shows
2. A show has a seating layout — seats are categorized (regular, premium, recliner)
3. User searches by movie + city → sees available shows → selects seats → books
4. Seats selected by one user must be temporarily locked (5 min) to prevent double booking
5. Different pricing per seat category and show time (matinee vs evening)
6. On booking confirmation, send ticket via email/SMS

---

## Clarifying Questions to Ask

- Can a user book seats across multiple shows in one transaction?
- What happens to a locked seat if payment fails?
- Is the seat lock per-user or does it block all users?
- How are group bookings handled (10 seats together)?
- Is there a waitlist if all seats are booked?
- Are there discounts for loyalty members or bulk bookings?

---

## Key Patterns (Hints)

<details>
<summary>Click to reveal</summary>

- **Factory** — create show, seat, booking objects
- **Strategy** — pricing per category and show time
- **Observer** — send confirmation on successful booking
- **State** — seat states: Available → Locked → Booked → Released
- **Singleton** — booking service (with caveats)

</details>

---

## Your Task

1. Core entities: `Movie`, `Theatre`, `Screen`, `Show`, `Seat`, `Booking`, `User`
2. Implement seat locking with a time-based release
3. Concurrent users trying to book same seat — only one should succeed
4. Implement in `src/main/java/com/lld/phase8/problems/classics/bookmyshow/`

---

## Edge Cases

- Last seat being booked by two users simultaneously
- User selects 4 seats but only 3 are confirmed (1 was snatched)
- Payment gateway timeout — booking in limbo state
- Show cancelled after bookings — refund all
