# Phase 9 — Advanced Topics (Staff Engineer Territory)
> Week 11 | Prerequisites: All previous phases complete
> Goal: Answer "How does your design handle concurrency / scale / failure?"

---

## Why This Phase Matters

At Senior level, you design the right system. At Staff level, you defend it under pressure:
- "What happens when two threads hit this simultaneously?"
- "How does this change if we have 10,000 events per second?"
- "How do you test this without a running database?"

This phase builds that vocabulary.

---

## Topic 1: Thread Safety in OOP

### The Core Problem
You've designed a beautiful `OrderService`. Then: two threads call `placeOrder()` for the same customer at 14:00:00.001. Both pass the inventory check. Both decrement stock. Stock goes to -1.

### The Vocabulary You Need

| Technique | When to Use | Trade-off |
|-----------|-------------|-----------|
| `synchronized` | Simple critical sections, low contention | Coarse lock → throughput loss |
| `volatile` | Single writer, multiple readers of a flag | Only guarantees visibility, not atomicity |
| `AtomicInteger` / `AtomicReference` | Lock-free counters, CAS operations | Only for single variables |
| `ReentrantLock` | Fine-grained locking, tryLock, timed lock | More boilerplate |
| `ReadWriteLock` | Many reads, few writes | Read-heavy systems only |
| Immutable objects | Share freely without synchronization | Requires upfront design |
| `ConcurrentHashMap` | Concurrent map access | Compound operations not atomic |

### Immutability as Your First Defense

```java
// BAD: mutable Money — thread-unsafe
public class Money {
    private double amount;
    public void add(double x) { this.amount += x; }  // NOT atomic
}

// GOOD: immutable Money — safely shared across threads
public record Money(BigDecimal amount, Currency currency) {
    public Money add(Money other) {
        return new Money(this.amount.add(other.amount), this.currency);
    }
}
```

### Concurrency Patterns

#### Producer-Consumer with `BlockingQueue`

```java
// Producer (e.g., HTTP request handler)
BlockingQueue<Order> orderQueue = new LinkedBlockingQueue<>(1000);

// In HTTP thread:
orderQueue.put(order);  // blocks if queue full

// Consumer (e.g., order processor thread pool)
Order order = orderQueue.take();  // blocks if queue empty
processOrder(order);
```

**LLD use case:** Async notification sending, log message buffering, order processing pipeline.

#### Read-Write Lock Pattern

```java
public class ProductCatalog {
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Map<String, Product> catalog = new HashMap<>();

    public Product find(String id) {
        lock.readLock().lock();
        try {
            return catalog.get(id);
        } finally {
            lock.readLock().unlock();
        }
    }

    public void update(Product product) {
        lock.writeLock().lock();
        try {
            catalog.put(product.id(), product);
        } finally {
            lock.writeLock().unlock();
        }
    }
}
```

**When to use:** Catalog reads >> catalog writes (product catalog, config, rates).

### Interview Checklist for Thread Safety
- [ ] Which objects are shared between threads?
- [ ] Which operations need to be atomic?
- [ ] Can I make data immutable to avoid locks?
- [ ] Is a `BlockingQueue` the right boundary between producers and consumers?
- [ ] What is my visibility guarantee (happens-before)?

---

## Topic 2: Domain-Driven Design (DDD)

### Why DDD Matters for LLD Interviews

DDD gives you a vocabulary for *where to put things*. Most LLD mistakes come from putting logic in the wrong place.

### Core Building Blocks

| Concept | Definition | Java Example |
|---------|-----------|--------------|
| **Entity** | Has identity, mutable state, lifecycle | `Order`, `Customer`, `Product` |
| **Value Object** | No identity, immutable, compared by value | `Money`, `Address`, `DateRange` |
| **Aggregate** | Cluster of entities/VOs with one root | `Order` (root) + `OrderItems` |
| **Aggregate Root** | The only entry point into the aggregate | `Order.addItem()`, `Order.cancel()` |
| **Repository** | Persistence abstraction for aggregates | `OrderRepository` |
| **Domain Service** | Logic that doesn't belong on any entity | `PricingService`, `MatchingService` |
| **Domain Event** | Something that happened in the domain | `OrderPlacedEvent`, `PaymentFailedEvent` |

### Entity vs Value Object — The Critical Distinction

```java
// Entity: identity matters — two orders with same items are DIFFERENT orders
public class Order {
    private final OrderId id;  // ← identity
    private OrderState state;
    private List<OrderItem> items;
    // equality based on id
}

// Value Object: value matters — two Money(100, USD) are THE SAME money
public record Money(BigDecimal amount, Currency currency) {
    // equality based on amount + currency, not object identity
    public boolean isGreaterThan(Money other) {
        return this.amount.compareTo(other.amount) > 0;
    }
}
```

### Aggregate Root — Enforcing Invariants

```java
// BAD: accessing internals directly breaks invariants
order.getItems().clear();  // bypasses business rules!

// GOOD: Aggregate Root controls all mutations
public class Order {
    private List<OrderItem> items = new ArrayList<>();

    public void addItem(Product product, int quantity) {
        if (state != OrderState.DRAFT) {
            throw new IllegalStateException("Cannot modify a confirmed order");
        }
        // business rule enforced HERE, not at caller
        items.add(new OrderItem(product, quantity));
    }
}
```

### Domain Events for Decoupling

```java
// Domain Event — what happened
public record OrderPlacedEvent(OrderId orderId, CustomerId customerId, Instant occurredAt) {}

// Aggregate publishes events
public class Order {
    private final List<DomainEvent> domainEvents = new ArrayList<>();

    public void place() {
        this.state = OrderState.PLACED;
        domainEvents.add(new OrderPlacedEvent(this.id, this.customerId, Instant.now()));
    }

    public List<DomainEvent> pullDomainEvents() {
        var events = List.copyOf(domainEvents);
        domainEvents.clear();
        return events;
    }
}

// Application layer dispatches events after saving
Order order = orderRepository.findById(orderId);
order.place();
orderRepository.save(order);
eventPublisher.publish(order.pullDomainEvents());  // now send notifications, etc.
```

---

## Topic 3: CQRS (Command Query Responsibility Segregation)

### The Problem It Solves

Your `ProductService.getProductDetails()` needs: product info + average rating + stock level + seller info. If all this comes from one model, queries drag down your write throughput.

### The Core Idea

```
WRITE side (Commands):  → Validates business rules → Updates state → Emits events
READ side (Queries):    → Optimized read models → Denormalized → No business logic
```

### LLD-Level CQRS

```java
// WRITE side — rich domain model
public class OrderCommandService {
    public void placeOrder(PlaceOrderCommand cmd) {
        Order order = new Order(cmd.customerId(), cmd.items());
        order.validate();   // business rules
        orderRepository.save(order);
        eventBus.publish(order.pullDomainEvents());
    }
}

// READ side — thin, optimized view
public class OrderQueryService {
    public OrderSummaryView getOrderSummary(String orderId) {
        // reads from a denormalized read model (could be a different table/service)
        return orderReadRepository.findSummary(orderId);
    }
}

// Read model updated by event handler
@EventHandler
public class OrderReadModelUpdater {
    public void on(OrderPlacedEvent event) {
        // project into read model (denormalized, query-optimized)
        orderReadModel.upsert(new OrderSummaryView(event.orderId(), "PLACED", ...));
    }
}
```

### When to Suggest CQRS in Interviews

✅ Different read vs write scalability needs  
✅ Complex query requirements (search, aggregations, joins)  
✅ Audit log / event history is a requirement  
❌ Simple CRUD apps — CQRS adds complexity without benefit  

---

## Topic 4: Event-Driven Design

### Event Sourcing

Instead of storing current state, store the sequence of events that led to it.

```java
// Traditional: store current state
// order.status = "SHIPPED"

// Event Sourcing: store events
// [OrderPlaced, PaymentConfirmed, WarehousePicked, Shipped]

public class Order {
    private List<DomainEvent> events = new ArrayList<>();

    // Rebuild state from events
    public static Order reconstitute(List<DomainEvent> history) {
        Order order = new Order();
        history.forEach(order::apply);
        return order;
    }

    private void apply(DomainEvent event) {
        switch (event) {
            case OrderPlacedEvent e -> this.state = OrderState.PLACED;
            case OrderShippedEvent e -> {
                this.state = OrderState.SHIPPED;
                this.trackingId = e.trackingId();
            }
            // ...
        }
    }
}
```

**Benefits:**
- Full audit trail for free
- Temporal queries ("what was the order state at 3pm yesterday?")
- Event replay to rebuild projections

**Costs:**
- Reading current state requires replaying all events (use snapshots for mitigation)
- Schema evolution of old events is hard

### Eventual Consistency

```
Traditional:  Write → DB updated → Read → See new data (strong consistency)
Event-Driven: Write → Event published → Consumer processes → Read model updated (eventual)

Gap between publish and read model update = "eventual consistency window"
```

**In interviews:** When asked "what if the notification service is down?", the answer is:
> "The event is persisted in the event store. The notification service will process it when it comes back online. The system is eventually consistent."

---

## Topic 5: Exception Design

### Exception Hierarchy (do this in every LLD)

```java
// Base domain exception
public class DomainException extends RuntimeException {
    private final ErrorCode errorCode;
    public DomainException(ErrorCode code, String message) {
        super(message);
        this.errorCode = code;
    }
}

// Specific exceptions
public class InsufficientStockException extends DomainException {
    public InsufficientStockException(String sku, int requested, int available) {
        super(ErrorCode.INSUFFICIENT_STOCK,
              "SKU %s: requested %d, available %d".formatted(sku, requested, available));
    }
}

public class InvalidStateTransitionException extends DomainException { ... }
public class OrderNotFoundException extends DomainException { ... }
```

### Checked vs Unchecked — The Production Answer

| Type | When | Java Convention |
|------|------|-----------------|
| **Unchecked** (`RuntimeException`) | Caller cannot reasonably recover | Programmer error, broken invariant |
| **Checked** (`Exception`) | Caller must handle explicitly | External system failure, user input error |

> **Interview answer:** "I use unchecked exceptions for domain violations (you broke a business rule — the caller should have validated first). I use checked exceptions for infrastructure failures (network timeout, file missing) where the caller genuinely needs to decide: retry, fallback, or propagate."

---

## Topic 6: API Design for Interfaces

### Principles for Designing Java Interfaces

```java
// BAD: interface that evolves badly
public interface PaymentProcessor {
    void process(String cardNumber, double amount, String currency, boolean save);
    // Adding a parameter breaks all implementations
}

// GOOD: request object makes it backward compatible
public interface PaymentProcessor {
    PaymentResult process(PaymentRequest request);
}

// GOOD: default methods allow backward-compatible additions
public interface NotificationChannel {
    void send(Notification notification);

    default boolean isHealthy() {   // new method, won't break existing impls
        return true;
    }
}
```

### Designing for Testability

```java
// HARD to test (hidden dependency)
public class OrderService {
    private final EmailClient emailClient = new EmailClient();  // new = untestable
}

// EASY to test (dependency injected)
public class OrderService {
    private final NotificationPort notifications;  // interface — mock in tests

    public OrderService(NotificationPort notifications) {
        this.notifications = notifications;
    }
}

// Test:
var mockNotifications = mock(NotificationPort.class);
var service = new OrderService(mockNotifications);
service.placeOrder(order);
verify(mockNotifications).notify(any(OrderPlacedEvent.class));
```

---

## Quick Reference: Interview Answers for Advanced Topics

| Question | Answer |
|----------|--------|
| "How do you handle concurrent reservations?" | Optimistic locking (version field), CAS, or database-level FOR UPDATE |
| "What if a service call fails midway through a transaction?" | Saga pattern: compensating transactions per step |
| "How do you scale this?" | CQRS separates read/write; domain events decouple services |
| "How do you test this?" | Dependency injection + ports-and-adapters; unit test domain logic in isolation |
| "What is eventual consistency and when is it acceptable?" | Read models lag behind writes. Acceptable for notifications, search indexes, dashboards. Not acceptable for inventory, payments. |

---

## Practice Problems in This Phase

| Problem | Key Concepts | Difficulty |
|---------|-------------|------------|
| [Distributed Rate Limiter](./problems/DistributedRateLimiter/PROBLEM.md) | Distributed state, Redis, CAS | Staff |
| [Workflow Engine](./problems/WorkflowEngine/PROBLEM.md) | DAG, Saga, compensation | Staff |
| [Event Sourcing System](./problems/EventSourcingSystem/PROBLEM.md) | Event store, projections, snapshots | Staff |
| [Rule Engine](./problems/RuleEngine/PROBLEM.md) | Interpreter, hot reload, DSL | Staff |
| [Feature Flag System](./problems/FeatureFlagSystem/PROBLEM.md) | Gradual rollout, targeting, kill switch | Staff |

---

## Mock Interview #9 — Prep Checklist

Before your mock:
- [ ] Can you explain volatile vs synchronized in one sentence each?
- [ ] Can you draw the CQRS flow on a whiteboard?
- [ ] Can you explain what an Aggregate Root is and WHY it matters?
- [ ] Can you design a Domain Event and show who publishes/subscribes?
- [ ] Can you answer "how do you test this without a database?"
