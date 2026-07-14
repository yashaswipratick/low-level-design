# Phase 4 — Object Relationships
> Week 1 Day 3–4 | Goal: Nail the Association / Aggregation / Composition distinction that trips up senior engineers.

---

## Why This Matters in Interviews

"Draw the class diagram for this system" → your UML arrows reveal whether you understand ownership and lifetime. Wrong relationships = wrong memory model = poor design decisions.

---

## The Four Relationships

```
DEPENDENCY       ──────>    "uses" (weakest)
ASSOCIATION      ────────   "knows about" / "has a"
AGGREGATION      ◇───────   "has a" (but doesn't own)
COMPOSITION      ◆───────   "owns" (lifecycle coupled)
```

---

## 1. Dependency — "uses temporarily"

```java
// Class A depends on class B if it uses B anywhere in its methods
// B is NOT stored as a field

public class OrderService {
    // Dependency: DiscountCalculator is created/used locally, not stored
    public Money calculateTotal(Order order) {
        DiscountCalculator calculator = new DiscountCalculator();
        return calculator.apply(order.subtotal(), order.coupons());
    }
}
```

**Memory model:** `DiscountCalculator` exists only during the method call. `OrderService` does not control its lifecycle.

**UML:** Dashed arrow `- - - - →`

---

## 2. Association — "knows about"

```java
// Class A holds a reference to Class B, but doesn't own it
// B can exist independently of A; B may be shared by multiple A instances

public class Order {
    private Customer customer;  // Order "knows about" Customer, doesn't own it

    // Customer exists independently — if Order is deleted, Customer still exists
}

// Bi-directional association:
public class Employee {
    private Department department;
}

public class Department {
    private List<Employee> employees;  // both know about each other
}
```

**Memory model:** B exists independently. A just holds a reference. Deleting A does not delete B.

**UML:** Solid line (no diamond), with multiplicity (1, *, 1..*)

---

## 3. Aggregation — "has a (but doesn't own)"

```java
// "Whole-Part" relationship, but parts can exist without the whole
// If the "whole" is destroyed, the "parts" still exist

public class Team {
    private List<Player> players;  // Team aggregates Players
    // If Team is disbanded, Players still exist (they join another team)
}

public class Playlist {
    private List<Song> songs;  // Playlist aggregates Songs
    // Delete the playlist, songs still exist in the library
}

public class University {
    private List<Department> departments;  // University aggregates Departments?
    // Or does it own them? (this is the debated edge case)
}
```

**Memory model:** Parts are created and managed externally. Aggregator holds references.

**UML:** Hollow diamond on the "whole" side `◇────`

---

## 4. Composition — "owns and is responsible for"

```java
// "Whole-Part" relationship where the part cannot exist without the whole
// The whole creates the part and is responsible for its lifecycle

public class Order {
    private final List<OrderItem> items = new ArrayList<>();  // Order OWNS OrderItems

    // OrderItem cannot exist without an Order — they're created together
    public void addItem(Product product, int qty) {
        items.add(new OrderItem(product, qty));  // Order creates it
    }
    // When Order is garbage collected, OrderItems are too
}

public class House {
    private final Room livingRoom = new Room("Living Room");  // House owns Rooms
    private final Room bedroom = new Room("Bedroom");
    // A Room doesn't exist independently of the House
}

public class HtmlDocument {
    private final List<HtmlElement> elements = new ArrayList<>();
    // Elements don't exist outside this document
}
```

**Memory model:** Parts are created by the whole. Deleting the whole deletes all parts.

**UML:** Filled diamond on the "whole" side `◆────`

---

## The Key Questions to Ask (in UML and in interviews)

```
Can B exist without A?
  YES → Association or Aggregation
  NO  → Composition

Can B be shared between multiple A instances?
  YES → Association or Aggregation
  NO  → Composition (usually)

Does A create B?
  YES → Composition
  NO  → Association or Aggregation

Is the relationship navigable in one or both directions?
  ONE WAY  → use arrow (A → B means A knows B but B doesn't know A)
  BOTH WAY → plain line (each knows the other)
```

---

## Spring Boot Connection

Spring's `@Autowired` creates **dependency** (via constructor injection) or **association** (via field injection).

```java
// Association: OrderService "knows about" OrderRepository
// But doesn't create it — Spring creates it (IoC)
@Service
public class OrderService {
    private final OrderRepository orderRepository;  // Association

    public OrderService(OrderRepository orderRepository) {  // Spring injects it
        this.orderRepository = orderRepository;
    }
}

// OrderRepository is NOT owned by OrderService
// It can be injected into 10 other services too
```

This is why **Dependency Injection ≠ Composition**. Spring creates both and wires them — the lifecycles are managed externally, so it's association (or dependency), not composition.

---

## Real-World Examples to Know Cold

| Relationship | Example |
|-------------|---------|
| **Composition** | `Order` → `OrderItem` (items don't exist without the order) |
| **Composition** | `House` → `Room` (rooms are part of the house structure) |
| **Composition** | `Document` → `Page` (pages make no sense outside the document) |
| **Aggregation** | `Team` → `Player` (players can leave and join other teams) |
| **Aggregation** | `Playlist` → `Song` (songs exist in the library, playlist references them) |
| **Aggregation** | `Department` → `Employee` (employees exist if dept is deleted) |
| **Association** | `Customer` → `Order` (customer knows their orders; orders have a customer ref) |
| **Association** | `Order` → `Customer` (order references customer; customer exists independently) |
| **Dependency** | `Controller` → `Request/Response` (used in method, not stored) |

---

## The Debate: Aggregation vs Composition in Practice

In code, the distinction is **who creates the part**:
- If A's constructor creates B: Composition
- If B is passed to A's constructor (injected): Association/Aggregation

```java
// Composition: House creates its own rooms
class House {
    private Room room = new Room();  // created here = owned here = COMPOSITION
}

// Aggregation: Playlist receives songs from outside
class Playlist {
    Playlist(List<Song> songs) {  // received, not created = AGGREGATION
        this.songs = songs;
    }
}
```

---

## Dependency — The Silent Coupling Killer

Dependencies are the most underrated relationship. Every `import` creates a dependency. Too many dependencies = shotgun surgery when anything changes.

```java
// This class has hidden dependencies on:
// - Java standard library (List, Optional)
// - Stripe's SDK (StripeException)
// - Spring's @Autowired
// - Your own domain types

@Service
public class PaymentService {
    @Autowired private StripeClient stripe;           // dependency on Stripe SDK
    @Autowired private OrderRepository orderRepo;     // dependency on persistence

    public void charge(Order order) throws StripeException {  // dependency on Stripe exception
        // ...
    }
}
```

**Fix:** Minimize, hide, and invert dependencies. Use interfaces at boundaries.

---

## Practice Problems

### Easy
1. For each pair, state the relationship type: `Car` and `Engine`; `Library` and `Book`; `Author` and `Book`; `Shelf` and `Book`.

### Medium
2. Draw the relationships in an Order Management system: `Order`, `OrderItem`, `Customer`, `Product`, `Address`, `Payment`.

### Hard
3. Design a University system. `University`, `Faculty`, `Department`, `Professor`, `Student`, `Course`, `Enrollment`. Justify every relationship type with memory lifecycle reasoning.
