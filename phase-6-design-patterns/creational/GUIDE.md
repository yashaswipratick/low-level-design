# Phase 6 — Creational Patterns
> Week 3 Day 1–3 | WHEN to use each pattern is more important than HOW.

---

## Creational Patterns Overview

| Pattern | Intent | Use When |
|---------|--------|----------|
| **Singleton** | One instance globally | Shared resource (config, connection pool, logger) |
| **Factory Method** | Subclass decides which object to create | Type of object known only at runtime |
| **Abstract Factory** | Family of related objects | UI kits, cross-platform support |
| **Builder** | Step-by-step complex object construction | Many optional parameters, immutable objects |
| **Prototype** | Clone existing objects | Expensive creation, copy-then-modify |

---

## Singleton — 6 Implementations, Thread Safety

### The Problem
You want exactly one instance of a class. The naive implementation is not thread-safe.

### Implementation 1: Eager (simplest, thread-safe)

```java
public class ConfigManager {
    private static final ConfigManager INSTANCE = new ConfigManager();  // eager init

    private ConfigManager() { }  // prevent external instantiation

    public static ConfigManager getInstance() { return INSTANCE; }
}
// Thread-safe because class loading is thread-safe
// Downside: always created even if never used
```

### Implementation 2: Double-Checked Locking (thread-safe, lazy)

```java
public class ConfigManager {
    private static volatile ConfigManager instance;  // volatile is REQUIRED

    private ConfigManager() { }

    public static ConfigManager getInstance() {
        if (instance == null) {                        // first check (no lock)
            synchronized (ConfigManager.class) {
                if (instance == null) {                // second check (inside lock)
                    instance = new ConfigManager();
                }
            }
        }
        return instance;
    }
}
// volatile prevents CPU reordering: object reference published before constructor completes
```

### Implementation 3: Enum Singleton (BEST for production)

```java
public enum DatabaseConnectionPool {
    INSTANCE;

    private final Connection connection;

    DatabaseConnectionPool() {
        this.connection = createConnection();
    }

    public Connection getConnection() { return connection; }
}

// Usage: DatabaseConnectionPool.INSTANCE.getConnection()
// Thread-safe, serialization-safe, reflection-safe — the definitive Java Singleton
```

### Interview Answer: "Why enum Singleton?"

> "Enum Singleton is the recommended Java Singleton because:
> 1. JVM guarantees each enum constant is created exactly once
> 2. Handles serialization automatically (no extra code)
> 3. Protected against reflection attacks (constructors can't be invoked reflectively)
> 4. No synchronization code needed"

### When NOT to Use Singleton

- When you need testability (singletons make mocking hard — use Spring beans instead)
- When the state is mutable and shared across threads (massive concurrency risk)
- When different tests need different configurations

---

## Factory Method — Runtime Object Type Decision

### The Problem
You have a method that creates objects, but subclasses should decide the exact type.

```java
// Without Factory Method: tightly coupled
public class NotificationService {
    public void notify(User user) {
        EmailNotifier notifier = new EmailNotifier();  // hardcoded type
        notifier.send(user);
    }
}

// With Factory Method
public abstract class NotificationService {
    public void notify(User user) {
        Notifier notifier = createNotifier();  // Factory Method
        notifier.send(user);
    }

    protected abstract Notifier createNotifier();  // subclasses decide type
}

public class EmailNotificationService extends NotificationService {
    @Override
    protected Notifier createNotifier() {
        return new EmailNotifier();
    }
}

public class SmsNotificationService extends NotificationService {
    @Override
    protected Notifier createNotifier() {
        return new SmsNotifier();
    }
}
```

### Simple Factory vs Factory Method (Common Confusion)

```java
// Simple Factory (NOT a Gang of Four pattern, but common in practice)
public class NotifierFactory {
    public static Notifier create(String type) {
        return switch (type) {
            case "EMAIL" -> new EmailNotifier();
            case "SMS"   -> new SmsNotifier();
            default      -> throw new IllegalArgumentException("Unknown: " + type);
        };
    }
}

// Factory Method (actual GoF) — subclasses override a creation method
// Use when: you have a class hierarchy and each subclass creates a different product
```

---

## Abstract Factory — Family of Related Objects

### The Problem
You need to create families of related objects without specifying their concrete classes.

```java
// Abstract Factory: UI toolkit
public interface UIComponentFactory {
    Button createButton();
    TextField createTextField();
    Dialog createDialog();
}

// Concrete factories create families
public class WindowsUIFactory implements UIComponentFactory {
    public Button createButton()       { return new WindowsButton(); }
    public TextField createTextField() { return new WindowsTextField(); }
    public Dialog createDialog()       { return new WindowsDialog(); }
}

public class MacUIFactory implements UIComponentFactory {
    public Button createButton()       { return new MacButton(); }
    public TextField createTextField() { return new MacTextField(); }
    public Dialog createDialog()       { return new MacDialog(); }
}

// Client: never knows which concrete classes it's using
public class Application {
    private final UIComponentFactory factory;

    public Application(UIComponentFactory factory) {
        this.factory = factory;
    }

    public void render() {
        Button btn = factory.createButton();   // Windows or Mac, doesn't matter
        btn.render();
    }
}
```

---

## Builder — Telescoping Constructor Solution

### The Problem
Objects with many optional parameters lead to "telescoping constructors" or JavaBeans-style setters (mutable, dangerous).

```java
// BAD: Telescoping constructors
new Order(customerId, productId, quantity)
new Order(customerId, productId, quantity, coupon)
new Order(customerId, productId, quantity, coupon, address)
new Order(customerId, productId, quantity, coupon, address, isExpressDelivery)
// Unmaintainable after 4-5 parameters
```

```java
// GOOD: Builder pattern — fluent, readable, immutable result
public final class Order {
    private final String customerId;       // required
    private final String productId;        // required
    private final int quantity;            // required
    private final String couponCode;       // optional
    private final Address address;         // optional
    private final boolean expressDelivery; // optional

    private Order(Builder builder) {       // private constructor
        this.customerId = builder.customerId;
        this.productId = builder.productId;
        this.quantity = builder.quantity;
        this.couponCode = builder.couponCode;
        this.address = builder.address;
        this.expressDelivery = builder.expressDelivery;
    }

    public static class Builder {
        // Required params in constructor
        private final String customerId;
        private final String productId;
        private final int quantity;

        // Optional params with defaults
        private String couponCode = null;
        private Address address = null;
        private boolean expressDelivery = false;

        public Builder(String customerId, String productId, int quantity) {
            this.customerId = customerId;
            this.productId = productId;
            this.quantity = quantity;
        }

        public Builder couponCode(String code)     { this.couponCode = code; return this; }
        public Builder address(Address addr)        { this.address = addr; return this; }
        public Builder expressDelivery(boolean exp) { this.expressDelivery = exp; return this; }

        public Order build() {
            // validate here
            if (quantity <= 0) throw new IllegalArgumentException("quantity must be > 0");
            return new Order(this);
        }
    }
}

// Usage: reads like natural language
Order order = new Order.Builder("cust-1", "prod-abc", 2)
    .couponCode("SAVE10")
    .address(deliveryAddress)
    .expressDelivery(true)
    .build();
```

### Lombok @Builder (Production Shortcut)

```java
// In production Spring Boot code, use Lombok:
@Builder
@Value  // immutable
public class CreateOrderRequest {
    String customerId;
    String productId;
    int quantity;
    @Builder.Default boolean expressDelivery = false;
}
```

---

## Prototype — Clone Instead of Create

### The Problem
Creating an object is expensive (network call, heavy computation). You want to copy an existing one and tweak it.

```java
public interface Prototype<T> {
    T clone();
}

public class ProductTemplate implements Prototype<ProductTemplate> {
    private String category;
    private List<String> tags;
    private Map<String, Object> attributes;

    // Shallow copy — fast but shares references
    @Override
    public ProductTemplate clone() {
        try {
            return (ProductTemplate) super.clone();  // Object.clone()
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    // Deep copy — safe but slower
    public ProductTemplate deepCopy() {
        ProductTemplate copy = new ProductTemplate();
        copy.category = this.category;
        copy.tags = new ArrayList<>(this.tags);              // new list
        copy.attributes = new HashMap<>(this.attributes);    // new map
        return copy;
    }
}

// Usage: create 100 products based on a template
ProductTemplate electronicsTemplate = new ProductTemplate("Electronics", ...);
Product laptop = electronicsTemplate.deepCopy();
laptop.setName("MacBook Pro");
laptop.setPrice(Money.of(2000));
```

### Interview Gotcha: Shallow vs Deep Copy

```java
// Shallow copy: primitives and immutable types are copied;
//               mutable objects (List, Map) share the reference

// Deep copy: everything is duplicated — fully independent

// The Cloneable interface in Java is broken (it's a marker interface, not a functional one)
// Prefer: copy constructor or serialization-based deep copy
```

---

## Pattern Selection Quick Guide

| Situation | Pattern |
|-----------|---------|
| Need exactly one instance | Singleton (enum) |
| Need to create objects but don't know which type until runtime | Factory Method |
| Need families of related objects (theme, platform) | Abstract Factory |
| Object has 5+ parameters, some optional, want immutability | Builder |
| Object creation is expensive, need many similar objects | Prototype |
| Creating objects based on config/string type | Simple Factory (+ Registry) |
