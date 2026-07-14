# Phase 6 — Structural Patterns
> Week 3 Day 4–7 | These patterns are about COMPOSING objects and classes into larger structures.

---

## Structural Patterns Overview

| Pattern | Intent | When to Use |
|---------|--------|-------------|
| **Adapter** | Convert one interface to another | Integrating legacy/third-party code |
| **Facade** | Simplified interface to complex subsystem | Reduce coupling to subsystems |
| **Decorator** | Add behavior dynamically without subclassing | Stack behaviors, add cross-cutting concerns |
| **Proxy** | Control access to another object | Lazy loading, security, logging, caching |
| **Composite** | Tree of objects treated uniformly | File system, UI trees, org charts |
| **Bridge** | Separate abstraction from implementation | Avoid class explosion in two dimensions |
| **Flyweight** | Share fine-grained objects to save memory | Thousands of similar objects |

---

## Adapter — "The Universal Plug"

### The Problem
You have an existing system that expects interface A, and a third-party library that provides interface B. You can't change either.

```java
// Target interface: what your system expects
public interface PaymentGateway {
    PaymentResult charge(PaymentRequest request);
}

// Adaptee: Stripe's API (can't modify it)
public class StripeClient {
    public StripeCharge createCharge(String currency, long amountCents, String token) { ... }
}

// Adapter: bridges the gap
public class StripePaymentAdapter implements PaymentGateway {
    private final StripeClient stripeClient;

    @Override
    public PaymentResult charge(PaymentRequest request) {
        // Adapt: PaymentRequest → StripeClient's format
        StripeCharge charge = stripeClient.createCharge(
            request.currency().code(),
            request.amount().toCents(),
            request.paymentToken()
        );
        // Adapt: StripeCharge → PaymentResult
        return PaymentResult.success(charge.getId(), request.amount());
    }
}
```

**Interview connection:** "The Adapter in our system is `StripePaymentAdapter`. When we switch to Braintree, we write `BraintreePaymentAdapter` — no changes to `PaymentService`."

---

## Facade — "Your @Service Layer IS a Facade"

### The Problem
Subsystems are complex. Clients shouldn't need to know about all the moving parts.

```java
// Subsystems (complex, many moving parts)
class InventoryService      { void reserve(String sku, int qty) { ... } }
class PaymentService        { PaymentResult charge(PaymentRequest req) { ... } }
class FulfillmentService    { void allocate(Order order) { ... } }
class NotificationService   { void notify(User user, OrderEvent event) { ... } }

// Facade: simplified interface for placing an order
@Service
public class OrderFacade {
    // ...dependencies injected...

    // Client calls one method, Facade orchestrates all subsystems
    public OrderResult placeOrder(PlaceOrderCommand command) {
        inventoryService.reserve(command.sku(), command.quantity());
        PaymentResult payment = paymentService.charge(command.paymentRequest());
        Order order = Order.create(command, payment);
        fulfillmentService.allocate(order);
        notificationService.notify(command.customer(), new OrderPlacedEvent(order));
        return OrderResult.success(order.getId());
    }
}
```

**Real-world connection:** Every Spring Boot `@Service` class that coordinates multiple repositories and other services IS a facade. When you interview at Walmart, mention this explicitly.

---

## Decorator — "Stack Behaviors Without Subclassing"

### The Problem
You want to add behaviors to objects at runtime. Inheritance would create a class explosion.

```java
// Without Decorator: class explosion
class EmailNotifier { }
class RateLimitedEmailNotifier extends EmailNotifier { }
class DeduplicatingEmailNotifier extends EmailNotifier { }
class DeduplicatingRateLimitedEmailNotifier extends EmailNotifier { }  // ugh
```

```java
// With Decorator: compose behaviors
public interface NotificationChannel {
    void send(Notification notification);
}

// Concrete component
public class EmailChannel implements NotificationChannel {
    @Override
    public void send(Notification notification) {
        // actually send email
    }
}

// Base decorator
public abstract class NotificationDecorator implements NotificationChannel {
    protected final NotificationChannel delegate;

    protected NotificationDecorator(NotificationChannel delegate) {
        this.delegate = delegate;
    }
}

// Concrete decorators
public class DeduplicatingDecorator extends NotificationDecorator {
    private final Set<String> recentlySent = new HashSet<>();

    @Override
    public void send(Notification notification) {
        if (!recentlySent.contains(notification.id())) {
            recentlySent.add(notification.id());
            delegate.send(notification);  // pass to wrapped channel
        }
    }
}

public class RateLimitingDecorator extends NotificationDecorator {
    private final RateLimiter limiter;

    @Override
    public void send(Notification notification) {
        if (limiter.allowRequest(notification.userId())) {
            delegate.send(notification);
        }
    }
}

// Compose at runtime
NotificationChannel channel = new RateLimitingDecorator(
    new DeduplicatingDecorator(
        new EmailChannel()
    )
);
channel.send(notification);  // → rate limit check → dedup check → email send
```

**Java standard library:** `java.io.InputStream` → `BufferedInputStream` → `GZIPInputStream` is the textbook Decorator chain.

---

## Proxy — "Control Access"

### Three Types of Proxy

#### 1. Virtual Proxy (Lazy Loading)
```java
public class LazyProductImageProxy implements ProductImage {
    private final String imageUrl;
    private byte[] imageData;  // loaded only when needed

    @Override
    public byte[] getImageData() {
        if (imageData == null) {
            imageData = imageLoader.load(imageUrl);  // expensive, deferred
        }
        return imageData;
    }
}
```

#### 2. Protection Proxy (Access Control)
```java
public class SecureDocumentProxy implements Document {
    private final Document realDocument;
    private final User currentUser;

    @Override
    public String getContent() {
        if (!currentUser.hasRole("READER")) {
            throw new AccessDeniedException("User cannot read this document");
        }
        return realDocument.getContent();
    }
}
```

#### 3. Logging/Metric Proxy (Cross-cutting concerns)
```java
public class TimedOrderRepository implements OrderRepository {
    private final OrderRepository delegate;
    private final MeterRegistry metrics;

    @Override
    public Order findById(String id) {
        long start = System.currentTimeMillis();
        try {
            return delegate.findById(id);
        } finally {
            metrics.timer("db.query.time").record(System.currentTimeMillis() - start, MILLISECONDS);
        }
    }
}
```

**Spring connection:** Spring AOP creates dynamic proxies at runtime. `@Transactional`, `@Cacheable`, `@Async` — all implemented via proxy. This is exactly Proxy pattern.

---

## Composite — "Tree of Uniform Objects"

### The Problem
Individual objects and compositions of objects should be treated uniformly.

```java
public interface FileSystemNode {
    String getName();
    long getSize();
    void display(String indent);
}

// Leaf
public class File implements FileSystemNode {
    private final String name;
    private final long size;

    @Override
    public long getSize() { return size; }

    @Override
    public void display(String indent) {
        System.out.println(indent + name + " (" + size + " bytes)");
    }
}

// Composite
public class Directory implements FileSystemNode {
    private final String name;
    private final List<FileSystemNode> children = new ArrayList<>();

    public void add(FileSystemNode node) { children.add(node); }

    @Override
    public long getSize() {
        return children.stream().mapToLong(FileSystemNode::getSize).sum();  // recursive
    }

    @Override
    public void display(String indent) {
        System.out.println(indent + name + "/");
        children.forEach(child -> child.display(indent + "  "));
    }
}

// Client code: uniform treatment
FileSystemNode root = new Directory("root");
root.display("");    // doesn't care if root is a File or Directory
long totalSize = root.getSize();  // recursion handled inside Composite
```

---

## Bridge — "Two Dimensions of Variation"

### The Problem
If you have both "types of shape" AND "rendering methods," inheritance creates N×M classes.

```java
// WITHOUT Bridge: class explosion
class CircleWithOpenGL {}
class CircleWithVulkan {}
class RectangleWithOpenGL {}
class RectangleWithVulkan {}

// WITH Bridge: separate hierarchies, connected by bridge
public interface Renderer {
    void renderCircle(double x, double y, double radius);
    void renderRectangle(double x, double y, double w, double h);
}

public class OpenGLRenderer implements Renderer { ... }
public class VulkanRenderer implements Renderer { ... }

public abstract class Shape {
    protected Renderer renderer;  // the "bridge" — links to renderer hierarchy

    public Shape(Renderer renderer) { this.renderer = renderer; }

    public abstract void draw();
}

public class Circle extends Shape {
    private double x, y, radius;

    @Override
    public void draw() {
        renderer.renderCircle(x, y, radius);  // delegates to renderer
    }
}
// Add new shape: no renderer changes. Add new renderer: no shape changes.
```

---

## Flyweight — "Java Already Uses This"

### The Problem
You need millions of similar objects. Their shared state is identical; only a small part differs.

```java
// Intrinsic state (shared, immutable) vs Extrinsic state (unique, passed in)

// String pool in Java: "hello".intern() — same char[] shared across all "hello" strings
// Integer.valueOf(-128 to 127) — cached, same object returned for common ints

// Application example: Character objects in a text editor
public class CharacterFlyweight {
    private final char character;   // intrinsic state (shared)
    private final Font font;        // intrinsic state (shared)
    private final Color color;      // intrinsic state (shared)

    // Position x,y is EXTRINSIC — passed in when rendering
    public void draw(int x, int y) {
        // render this.character at position x,y using this.font and this.color
    }
}

public class CharacterFlyweightFactory {
    private final Map<String, CharacterFlyweight> pool = new HashMap<>();

    public CharacterFlyweight getCharacter(char c, Font font, Color color) {
        String key = c + ":" + font + ":" + color;
        return pool.computeIfAbsent(key, k -> new CharacterFlyweight(c, font, color));
    }
}
// 1 million 'a' characters in Arial Black → 1 shared object, not 1 million
```

---

## Pattern Recognition Quick Reference

| You see in the problem... | Consider this pattern |
|---------------------------|-----------------------|
| "Integrate with legacy/third-party API" | **Adapter** |
| "Simplify complex subsystem" | **Facade** |
| "Add features at runtime / stack cross-cutting concerns" | **Decorator** |
| "Lazy loading / access control / logging around an object" | **Proxy** |
| "Tree structure / recursive operations on a hierarchy" | **Composite** |
| "Two independent dimensions of variation" | **Bridge** |
| "Millions of similar objects" | **Flyweight** |
