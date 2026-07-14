# Problem: Event Sourcing System
> Domain: Architecture Pattern | Difficulty: Staff | Est. Time: 90 min | Interview Frequency: 📌 Occasional at Staff Level

---

## Problem Statement

Design an Event Sourcing system for an Order domain.

Requirements:
1. Store every domain event as an immutable append-only record (no UPDATE/DELETE on event store)
2. Reconstruct current state of any aggregate by replaying its events
3. Snapshots: for aggregates with many events, create periodic snapshots to speed up replay
4. Projections: maintain denormalized read models from event streams
5. Event versioning: handle schema changes to old event types (upcasting)
6. Concurrent writes: prevent lost updates using optimistic concurrency (version numbers)
7. Event publishing: after persisting events, publish to event bus for downstream consumers

---

## Clarifying Questions to Ask

- Is this a single-service event store or shared across services (Event Store DB, Kafka)?
- What is the expected event rate per aggregate? (Determines snapshot frequency)
- Is event replay idempotent — can projections be rebuilt from scratch at any time?
- Is event ordering guaranteed (per-aggregate? globally)?
- Are long-running aggregates in scope (millions of events per order)?

---

## Key Concepts Required

- **Append-only log** — event store is a log, not a table of current state
- **Aggregate versioning** — optimistic locking via event sequence number
- **Snapshot store** — checkpoint aggregate state at version N
- **Projection** — event handler that builds a read model from event stream
- **Upcasting** — transform old event schemas to new ones at read time

---

## Class Design Starting Point

```java
// Domain Event — immutable record of what happened
public sealed interface DomainEvent permits
    OrderPlacedEvent, OrderConfirmedEvent, OrderShippedEvent, OrderCancelledEvent {}

public record OrderPlacedEvent(
    String aggregateId,
    long version,
    Instant occurredAt,
    String customerId,
    List<OrderLineDto> lines
) implements DomainEvent {}

// Event Store — append-only storage
public interface EventStore {
    void append(String aggregateId, List<DomainEvent> newEvents, long expectedVersion)
        throws OptimisticConcurrencyException;

    List<DomainEvent> loadEvents(String aggregateId);
    List<DomainEvent> loadEvents(String aggregateId, long fromVersion);
}

// Aggregate reconstruction
public class Order {
    private OrderState state;
    private long version;

    public static Order reconstitute(List<DomainEvent> events) {
        Order order = new Order();
        events.forEach(order::apply);
        return order;
    }

    private void apply(DomainEvent event) {
        switch (event) {
            case OrderPlacedEvent e -> {
                this.state = OrderState.PLACED;
                this.version = e.version();
                // ... set other fields
            }
            case OrderShippedEvent e -> this.state = OrderState.SHIPPED;
            // ...
        }
    }
}

// Snapshot
public record Snapshot(String aggregateId, long version, byte[] serializedState) {}

public interface SnapshotStore {
    Optional<Snapshot> loadLatest(String aggregateId);
    void save(Snapshot snapshot);
}

// Projection
public interface Projection {
    void on(DomainEvent event);
}
```

---

## Your Task

1. `EventStore` implementation (in-memory with `ConcurrentHashMap<String, List<DomainEvent>>`)
2. Optimistic concurrency: `append()` throws if `expectedVersion != actual current version`
3. `Order` aggregate with `reconstitute()` and full event application logic
4. `SnapshotStore` + `SnapshotStrategy` (snapshot every N events)
5. `OrderSummaryProjection` — builds `Map<OrderId, OrderSummaryView>` from event stream
6. `EventBus` — after persist, fan-out to all registered projections
7. Implement in `src/main/java/com/lld/phase9/eventsourcing/`

---

## Edge Cases

- Two users modify the same order simultaneously — both read version 5, both try to append at version 6 → one must fail
- Projection consumer is down when events are published — replay from event store on restart
- Event schema changed (v1: `amount` as `double`, v2: `amount` as `BigDecimal`) — upcaster
- Aggregate with 10,000 events — reconstitute without loading all events (use snapshot)
- Snapshot is corrupted — fall back to full event replay
- Event published to bus but transaction rolled back — phantom events downstream

---

## Trade-off Discussion Points

| Aspect | Event Sourcing Benefit | Event Sourcing Cost |
|--------|----------------------|---------------------|
| Audit log | Free — the log IS the store | None |
| Temporal queries | Replay to any point in time | Complex read path |
| Schema evolution | Old events preserved | Upcasting required |
| Current state read | Replay needed (mitigated by snapshots) | Higher read latency |
| Debugging | Full history available | Cognitive overhead |

> Key interview point: "Event Sourcing is not always the right choice. It adds significant complexity. Use it when: audit trail is a legal/compliance requirement, temporal queries are needed, or you need to rebuild projections from scratch."
