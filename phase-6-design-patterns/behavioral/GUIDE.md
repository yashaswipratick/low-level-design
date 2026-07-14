# Phase 6 — Behavioral Patterns
> Week 4 Day 1–5 | These are what interviewers love for REAL WORLD design problems.

---

## Behavioral Patterns Overview

| Pattern | One-liner | Classic Use Case |
|---------|-----------|-----------------|
| **Strategy** | Swap algorithms at runtime | Payment methods, sorting, pricing |
| **Observer** | Notify multiple subscribers | Event systems, notifications |
| **Command** | Encapsulate request as object | Undo/redo, queues, logging |
| **State** | Behavior changes with state | Order lifecycle, ATM, elevator |
| **Template Method** | Define skeleton, fill in details | Report generation, data pipelines |
| **Chain of Responsibility** | Pass request through handler chain | Middleware, filter chains, validation |
| **Iterator** | Traverse without knowing internals | Custom collections, tree traversal |
| **Mediator** | Centralize communication | Chat rooms, air traffic control |
| **Memento** | Capture and restore state | Undo, snapshots |
| **Visitor** | New operations without modifying classes | AST traversal, report generation |
| **Interpreter** | Evaluate grammar/expressions | Rule engines, query languages |

---

## Strategy — "Replace Switch Statements Forever"

### The Pattern

```java
// Without Strategy: the switch nightmare
public Money calculatePrice(Order order, String discountType) {
    return switch (discountType) {
        case "FLAT"       -> order.total().minus(Money.of(50));
        case "PERCENTAGE" -> order.total().times(0.9);
        case "BOGO"       -> order.total().times(0.5);
        // Adding "LOYALTY" requires modifying this method — OCP violation
    };
}

// With Strategy: pluggable, extensible
public interface DiscountStrategy {
    Money apply(Money originalTotal);
}

public record FlatDiscount(Money amount) implements DiscountStrategy {
    public Money apply(Money total) { return total.minus(amount); }
}

public record PercentageDiscount(double percent) implements DiscountStrategy {
    public Money apply(Money total) { return total.times(1 - percent); }
}

// Context class
public class PricingService {
    public Money calculatePrice(Order order, DiscountStrategy strategy) {
        return strategy.apply(order.total());
    }
}
// Adding LOYALTY: just add LoyaltyDiscountStrategy — no existing code changes
```

**Interview tip:** "I use Strategy whenever I see a `switch` on type, or when an algorithm needs to be swappable without changing the client."

---

## Observer — "Decouple Publishers from Subscribers"

### The Pattern

```java
// Observer interface
public interface InventoryObserver {
    void onInventoryChange(InventoryEvent event);
}

// Observable (Subject)
public class InventoryService {
    private final List<InventoryObserver> observers = new ArrayList<>();

    public void subscribe(InventoryObserver observer)   { observers.add(observer); }
    public void unsubscribe(InventoryObserver observer) { observers.remove(observer); }

    public void updateStock(String sku, int newQuantity) {
        // ... update stock ...
        var event = new InventoryEvent(sku, newQuantity);
        observers.forEach(obs -> obs.onInventoryChange(event));  // notify all
    }
}

// Concrete Observers
public class LowStockAlertObserver implements InventoryObserver {
    @Override
    public void onInventoryChange(InventoryEvent event) {
        if (event.newQuantity() < 10) {
            alertService.sendLowStockAlert(event.sku());
        }
    }
}

public class ReorderObserver implements InventoryObserver {
    @Override
    public void onInventoryChange(InventoryEvent event) {
        if (event.newQuantity() < 5) {
            purchaseOrderService.raiseReorder(event.sku());
        }
    }
}
```

**Spring connection:** `ApplicationEventPublisher` and `@EventListener` are Spring's Observer implementation. `@TransactionalEventListener` fires after the transaction commits.

---

## Command — "Encapsulate Operations"

### The Pattern

```java
// Command interface
public interface OrderCommand {
    void execute();
    void undo();   // optional, for undo/redo systems
}

// Concrete Commands
public class PlaceOrderCommand implements OrderCommand {
    private final Order order;
    private final OrderRepository repository;

    @Override
    public void execute() {
        order.place();
        repository.save(order);
    }

    @Override
    public void undo() {
        order.cancel();
        repository.save(order);
    }
}

public class CancelOrderCommand implements OrderCommand {
    private final Order order;
    private final RefundService refundService;

    @Override
    public void execute() {
        refundService.initiateRefund(order);
        order.cancel();
    }
}

// Invoker: executes and optionally tracks history
public class OrderCommandInvoker {
    private final Deque<OrderCommand> history = new ArrayDeque<>();

    public void execute(OrderCommand command) {
        command.execute();
        history.push(command);
    }

    public void undo() {
        if (!history.isEmpty()) {
            history.pop().undo();
        }
    }
}
```

**Use cases:** Undo/redo operations, queued task execution, audit logging of every operation, transactional scripts.

---

## State — "Objects That Change Behavior Based on State"

### The Problem
If you model state transitions with `if/else` chains, the code becomes unmaintainable as states grow.

```java
// WITHOUT State: messy if/else
public class Order {
    private String status;

    public void ship() {
        if (status.equals("CONFIRMED")) {
            status = "SHIPPED";
            notifyCustomer();
        } else if (status.equals("PLACED")) {
            throw new InvalidStateException("Must confirm before shipping");
        } else if (status.equals("SHIPPED")) {
            throw new InvalidStateException("Already shipped");
        }
        // Adding new state = modifying this method forever
    }
}
```

```java
// WITH State pattern: each state handles its own transitions
public interface OrderState {
    void confirm(Order order);
    void ship(Order order);
    void deliver(Order order);
    void cancel(Order order);
}

// Concrete states
public class PlacedState implements OrderState {
    @Override
    public void confirm(Order order) {
        order.setState(new ConfirmedState());
        order.notifyObservers(OrderEvent.CONFIRMED);
    }

    @Override
    public void ship(Order order) {
        throw new InvalidTransitionException("Cannot ship from PLACED state");
    }

    // ... other transitions
}

public class ConfirmedState implements OrderState {
    @Override
    public void ship(Order order) {
        order.setState(new ShippedState());
        order.notifyObservers(OrderEvent.SHIPPED);
    }

    @Override
    public void cancel(Order order) {
        order.setState(new CancelledState());
        // initiate refund if payment was made
    }
}

// Context (Order delegates to current state)
public class Order {
    private OrderState state = new PlacedState();

    public void confirm() { state.confirm(this); }
    public void ship()    { state.ship(this); }

    void setState(OrderState newState) { this.state = newState; }
}
```

---

## Template Method — "Hollywood Principle: Don't Call Us, We'll Call You"

### The Pattern

```java
// Abstract class defines the ALGORITHM skeleton
public abstract class ReportGenerator {
    // Template method — final so subclasses can't change the algorithm
    public final Report generate(ReportRequest request) {
        validateRequest(request);           // fixed step
        List<ReportData> data = fetchData(request);  // abstract — override this
        List<ReportData> processed = processData(data);  // abstract
        return formatReport(processed);     // abstract
    }

    private void validateRequest(ReportRequest request) {
        // validation logic, same for all reports
    }

    protected abstract List<ReportData> fetchData(ReportRequest request);
    protected abstract List<ReportData> processData(List<ReportData> data);
    protected abstract Report formatReport(List<ReportData> data);
}

// Concrete implementations fill in the blanks
public class SalesReportGenerator extends ReportGenerator {
    @Override
    protected List<ReportData> fetchData(ReportRequest request) {
        return salesRepository.findByDateRange(request.from(), request.to());
    }

    @Override
    protected List<ReportData> processData(List<ReportData> data) {
        return data.stream().filter(d -> d.amount().isPositive()).toList();
    }

    @Override
    protected Report formatReport(List<ReportData> data) {
        return new SalesReport(data);
    }
}
```

**Spring connection:** `JdbcTemplate`, `RestTemplate`, `HibernateTemplate` — all use Template Method. You override `mapRow()` or pass a callback lambda.

---

## Chain of Responsibility — "Pass the Buck"

### The Pattern

```java
// Handler interface
public abstract class ValidationHandler {
    private ValidationHandler next;

    public ValidationHandler setNext(ValidationHandler next) {
        this.next = next;
        return next;
    }

    public void validate(Order order) {
        if (canHandle(order)) {
            handle(order);
        }
        if (next != null) {
            next.validate(order);
        }
    }

    protected abstract boolean canHandle(Order order);
    protected abstract void handle(Order order);
}

// Concrete handlers
public class InventoryCheckHandler extends ValidationHandler {
    @Override
    protected boolean canHandle(Order order) { return true; }

    @Override
    protected void handle(Order order) {
        order.items().forEach(item -> {
            if (inventoryService.availableStock(item.sku()) < item.quantity()) {
                throw new InsufficientStockException(item.sku());
            }
        });
    }
}

public class FraudCheckHandler extends ValidationHandler {
    @Override
    protected void handle(Order order) {
        if (fraudDetector.isSuspicious(order)) {
            throw new FraudSuspectedException(order.id());
        }
    }
}

// Build the chain
ValidationHandler chain = new InventoryCheckHandler();
chain.setNext(new FraudCheckHandler())
     .setNext(new CouponValidationHandler());

chain.validate(order);  // passes through all handlers in sequence
```

**Spring Security** is the most famous Chain of Responsibility in the Java ecosystem. Every `Filter` in the `SecurityFilterChain` is a handler.

---

## Iterator — Custom Collection Traversal

```java
public class OrderHistory implements Iterable<Order> {
    private final List<Order> orders;

    @Override
    public Iterator<Order> iterator() {
        return new RecentFirstIterator(orders);
    }

    private static class RecentFirstIterator implements Iterator<Order> {
        private int index;
        private final List<Order> orders;

        RecentFirstIterator(List<Order> orders) {
            this.orders = orders;
            this.index = orders.size() - 1;  // start from end (most recent)
        }

        @Override
        public boolean hasNext() { return index >= 0; }

        @Override
        public Order next() {
            if (!hasNext()) throw new NoSuchElementException();
            return orders.get(index--);
        }
    }
}

// Usage — works with for-each!
for (Order order : customer.getOrderHistory()) { ... }
```

---

## Mediator — "Centralize Communication"

```java
// Without Mediator: UI components directly reference each other = spaghetti
// With Mediator: all components communicate through a central mediator

public interface ChatMediator {
    void sendMessage(String message, User from);
    void addUser(User user);
}

public class ChatRoom implements ChatMediator {
    private final List<User> users = new ArrayList<>();

    @Override
    public void addUser(User user) { users.add(user); }

    @Override
    public void sendMessage(String message, User from) {
        users.stream()
             .filter(u -> !u.equals(from))   // everyone except sender
             .forEach(u -> u.receive(message, from.getName()));
    }
}

public class User {
    private final String name;
    private final ChatMediator mediator;

    public void send(String message) {
        System.out.println(name + " sends: " + message);
        mediator.sendMessage(message, this);  // through mediator, not direct
    }

    public void receive(String message, String from) {
        System.out.println(name + " receives from " + from + ": " + message);
    }
}
```

---

## Pattern Combinations (What Real Systems Use)

| System | Patterns Combined |
|--------|------------------|
| Low Stock Alert System | Observer + Strategy + Chain of Responsibility + Decorator |
| Order Workflow | State + Command + Observer |
| Payment Processing | Strategy + Chain of Responsibility + Decorator |
| Notification System | Observer + Strategy + Decorator |
| Spring Security | Chain of Responsibility + Strategy + Proxy |
| Spring @Transactional | Proxy + Template Method |

---

## Quick Interview Decision Guide

```
"What varies?" or "What changes?"
  → Strategy (encapsulate the variation)

"Who needs to know when X happens?"
  → Observer (broadcast the change)

"I need to undo/log/queue this operation"
  → Command (encapsulate the operation)

"Object has multiple states, different behavior per state"
  → State (behavior changes with state)

"Same algorithm, different steps per subtype"
  → Template Method (skeleton + hooks)

"Multiple validators/filters in sequence"
  → Chain of Responsibility

"Many objects need to communicate without knowing each other"
  → Mediator
```
