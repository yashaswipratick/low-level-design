# Phase 5 — UML for Interviews
> Week 4 Day 6–7 | Goal: Draw a class diagram in 5 minutes on a whiteboard. Read sequence diagrams instantly.

---

## What Interviewers Actually Want

They don't want perfect UML. They want to see:
1. You can identify classes and their relationships quickly
2. You communicate design visually (saves verbal explanation time)
3. You know which diagram to use when

**Rule:** In an interview, sketch > formal. Readable > correct notation.

---

## Class Diagram — The Only Diagram You Must Know Cold

### Notation Reference (Memorize This)

```
CLASS:
┌─────────────────────┐
│    ClassName        │  ← Name compartment
├─────────────────────┤
│  - privateField     │  ← Attributes
│  # protectedField   │
│  + publicField      │
├─────────────────────┤
│  + method(): Type   │  ← Operations
│  - helper(): void   │
└─────────────────────┘

INTERFACE:
┌─────────────────────┐
│  <<interface>>      │
│  InterfaceName      │
├─────────────────────┤
│  + method(): Type   │
└─────────────────────┘

RELATIONSHIPS:
──────────────>    Dependency (uses)
────────────────   Association (has-a, knows-about)
──────────◇        Aggregation (has-a, doesn't own)
──────────◆        Composition (owns, lifecycle coupled)
──────────△        Inheritance (extends)
─ ─ ─ ─ ─△        Realization (implements)
```

### Example: Order System Class Diagram

```
                    <<interface>>
                   PaymentStrategy
                         △
          ┌──────────────┼──────────────┐
          │              │              │
   CreditCard         UPIPayment    WalletPayment


Order ◆────────── OrderItem ──────────> Product
  │                                       │
  │                                       │
  ├──────────────> Customer          (association)
  │
  └──────────────> Address


     <<interface>>
     OrderRepository
           △
           │
  MySQLOrderRepository
```

### How to Draw in an Interview (5-minute process)

```
Step 1 (1 min): Write all entity names as boxes
Step 2 (1 min): Fill in key attributes (3-5 per class, not all)
Step 3 (1 min): Fill in key methods (the ones that matter for the design)
Step 4 (2 min): Draw relationships — start with inheritance/realization,
                then composition, then association
```

**Interview tip:** Narrate while you draw. "I'm making `OrderItem` a composition of `Order` because an item can't exist without its parent order."

---

## Sequence Diagram — For Method Call Flows

Use when interviewer asks: "Walk me through what happens when a user places an order."

### Notation

```
Actor  :OrderController  :OrderService  :InventoryService  :OrderRepository
  │           │                │                 │                  │
  │──POST /order──────────────>│                 │                  │
  │           │──placeOrder()─>│                 │                  │
  │           │                │──checkStock()──>│                  │
  │           │                │<────ok──────────│                  │
  │           │                │──────────────────────────save()──>│
  │           │                │<─────────────────────────────ok───│
  │           │<────orderId────│                 │                  │
  │<──201 Created─────────────│                 │                  │
```

### When to Use Sequence Diagrams

- "How does notification delivery work?"
- "Walk me through the payment flow."
- "What happens when inventory goes low?"

---

## State Diagram — For Object Lifecycles

Use when an entity has distinct states and transitions matter.

### Order State Machine

```
                [Placed]
                    │
                    │ confirm()
                    ▼
               [Confirmed]
                    │
          ┌─────────┼──────────┐
    pack()│         │          │cancel()
          ▼         │          ▼
       [Packed]      │     [Cancelled]
          │         │
   ship() │         │
          ▼         │
       [Shipped]    │
          │         │
 deliver()│         │
          ▼         │
      [Delivered]   │
          │         │
   return │         │
          ▼         │
      [Returned]────┘
```

### Notation

```
○──────>[ State ]──event / [guard]──>[ State ]
        ↑                              │
        └──────────────────────────────┘
                  (self-transition)

○  = initial state (filled circle)
⊗  = final state (circle with X)
```

---

## Interview Whiteboard Strategy

### 30-Minute LLD Interview Timeline

```
0-5 min:   Requirements clarification (ask clarifying questions)
5-10 min:  Identify entities (write them as boxes quickly)
10-15 min: Draw class diagram with key relationships
15-20 min: Write core interfaces and 1-2 implementations
20-25 min: Draw sequence diagram for main flow
25-30 min: Discuss trade-offs and extensibility
```

### What Not to Do

❌ Start coding before drawing the diagram  
❌ Draw every field and method (pick the important ones)  
❌ Use perfect UML syntax (readable > formal in interviews)  
❌ Silent while drawing (narrate your thinking)  
❌ Forget to show design patterns on the diagram  

### Showing Design Patterns on UML

```
// Show Observer pattern:
Product ──────────────────────────────────────> <<interface>>
                                                 PriceObserver
                                                      △
                                               ┌──────┴───────┐
                                          EmailNotifier  SMSNotifier


// Show Strategy pattern:
PaymentService ─────────────────────────> <<interface>>
                                          PaymentStrategy
                                               △
                                         ┌─────┴────┐
                                    CreditCard    UPIPayment


// Show Decorator:
<<interface>>         ────────────────────> <<interface>>
NotificationChannel                        NotificationChannel
       △                                          △
       │                                          │
EmailChannel                             DeduplicatingDecorator
                                                  │ wraps
                                         NotificationChannel (any)
```

---

## UML Shorthand for Whiteboard

When drawing fast, use these shortcuts interviewers accept:

```
A ──> B         A depends on B
A ──── B        Association (bidirectional)
A ──▷ B         A extends B (inheritance)
A --▷ B         A implements B (realization)
A ◆── B         Composition (A owns B)
A ◇── B         Aggregation (A has B, doesn't own)

1     *          Multiplicity on lines
0..1  1..*
```

---

## Practice: Draw These Systems in 5 Minutes Each

1. Parking Lot (multi-level, vehicle types, payment)
2. BookMyShow (movie, show, seat, booking)
3. LRU Cache (cache, eviction policy, node)
4. Rate Limiter (limiter, algorithm strategy, client)
5. Low Stock Alert System (product, inventory, observer, channel)

For each:
- Box all classes
- Show inheritance/interface realization
- Show at least one composition and one association
- Label the design pattern being applied
