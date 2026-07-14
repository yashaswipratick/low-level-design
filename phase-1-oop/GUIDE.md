# Phase 1 — OOP Deep Dive (Accelerated for 10-Year Engineers)
> Week 1 | Goal: Fix the THINKING, not the syntax.

---

## Why This Phase Exists

After 10 years you know the syntax. What you need is the *vocabulary* to explain WHY you made a design decision and the *instinct* to spot when OOP is being violated. Interviewers don't ask "what is polymorphism?" — they show you code and ask "what's wrong here?"

---

## Pillar 1: Encapsulation — "Public Fields Are a Design Crime"

### The Real Definition
Not just "make fields private." Encapsulation means **hiding implementation details so you can change them without breaking callers.**

### The Violation You See Everywhere

```java
// BAD: Leaking internals — caller depends on the data structure
public class Order {
    public List<OrderItem> items = new ArrayList<>();  // caller can do order.items.clear()!
    public double totalAmount;                          // caller can set order.totalAmount = 0
}

// BAD: getter that returns mutable reference
public class Order {
    private List<OrderItem> items = new ArrayList<>();
    public List<OrderItem> getItems() { return items; }  // caller can still mutate it
}
```

```java
// GOOD: Encapsulated — hides storage, exposes behavior
public class Order {
    private final List<OrderItem> items = new ArrayList<>();
    private Money total = Money.ZERO;

    public void addItem(OrderItem item) {
        items.add(item);
        total = total.add(item.price());
    }

    public Money getTotal() { return total; }

    // Expose an unmodifiable view, not the internal list
    public List<OrderItem> getItems() { return Collections.unmodifiableList(items); }
}
```

### The Key Insight
"The class is the only code that knows it's using an `ArrayList`. Tomorrow you switch to a `LinkedList` or a database-backed list. No caller breaks."

---

## Pillar 2: Abstraction — The `abstract class` vs `interface` Decision Tree

```
Does the type define a CONTRACT (what it does, not how)?  → interface
Does the type share STATE or PARTIAL IMPLEMENTATION?      → abstract class
Does the type represent a CAPABILITY (can do X)?          → interface
Does the type represent a TYPE HIERARCHY (is-a)?          → abstract class
```

### The Production Decision

```java
// Interface: pure contract — any type can notify
public interface Notifiable {
    void notify(NotificationEvent event);
}

// Abstract class: shared state + template behavior
public abstract class BasePaymentProcessor {
    private final PaymentAuditLogger auditLogger;  // shared state

    public final PaymentResult process(PaymentRequest request) {
        auditLogger.logAttempt(request);            // shared behavior
        PaymentResult result = doProcess(request);  // abstract — override this
        auditLogger.logResult(result);              // shared behavior
        return result;
    }

    protected abstract PaymentResult doProcess(PaymentRequest request);
}
```

### Java 21 Answer for Interviews

```java
// Sealed interfaces + records = the modern answer to "how do you model a closed type hierarchy"
public sealed interface Shape permits Circle, Rectangle, Triangle {}

public record Circle(double radius) implements Shape {}
public record Rectangle(double width, double height) implements Shape {}

// Pattern matching switch — exhaustive, compiler-checked
double area = switch (shape) {
    case Circle c    -> Math.PI * c.radius() * c.radius();
    case Rectangle r -> r.width() * r.height();
    case Triangle t  -> /* ... */;
    // No default needed — sealed + exhaustive
};
```

---

## Pillar 3: Inheritance — When IS-A Becomes a Trap

### The Square-Rectangle Problem (Classic Interview Trap)

```java
// Mathematically: a square IS-A rectangle
// But in OOP: this violates Liskov Substitution Principle

class Rectangle {
    protected int width, height;
    void setWidth(int w) { this.width = w; }
    void setHeight(int h) { this.height = h; }
    int area() { return width * height; }
}

class Square extends Rectangle {
    @Override
    void setWidth(int w) {
        this.width = w;
        this.height = w;  // forced to keep square invariant
    }
    @Override
    void setHeight(int h) {
        this.width = h;
        this.height = h;
    }
}

// This breaks:
Rectangle r = new Square();
r.setWidth(5);
r.setHeight(3);
// Expected area: 15. Actual area: 9. Square changed width when you set height!
```

**The fix:** Don't model this with inheritance. Use composition or different hierarchies:
```java
// Option 1: No inheritance — separate classes, shared interface
public interface Shape { int area(); }
public record Rectangle(int width, int height) implements Shape { ... }
public record Square(int side) implements Shape { ... }
```

### Composition Over Inheritance (Prefer Always)

```java
// BAD: inheritance for reuse
class EmailLogger extends Logger {
    void logEmail(String to, String subject) {
        log("Email to: " + to);  // using parent's log()
        sendEmail(to, subject);
    }
}

// GOOD: composition for reuse
class EmailService {
    private final Logger logger;  // HAS-A, not IS-A

    EmailService(Logger logger) { this.logger = logger; }

    void sendEmail(String to, String subject) {
        logger.log("Sending email to: " + to);
        // send...
    }
}
```

**Rule:** Prefer composition. Use inheritance only when you have a genuine IS-A relationship AND you want polymorphic substitutability.

---

## Pillar 4: Polymorphism — Runtime Dispatch

### Method Overriding vs Method Hiding

```java
class Animal {
    void speak() { System.out.println("..."); }          // instance method
    static void staticSpeak() { System.out.println("Animal"); }  // class method
}

class Dog extends Animal {
    @Override
    void speak() { System.out.println("Woof"); }         // OVERRIDES — runtime dispatch
    static void staticSpeak() { System.out.println("Dog"); }     // HIDES — compile-time
}

Animal a = new Dog();
a.speak();         // Woof    ← runtime dispatch (polymorphism works)
a.staticSpeak();   // Animal  ← static binding (no polymorphism for static)
```

### The Power of Polymorphism

```java
// Without polymorphism
void processPayment(Object payment) {
    if (payment instanceof CreditCard) { /* ... */ }
    else if (payment instanceof UPI) { /* ... */ }
    else if (payment instanceof Wallet) { /* ... */ }
    // Every new type requires modifying this method
}

// With polymorphism
void processPayment(PaymentMethod payment) {
    payment.charge(amount);  // runtime dispatch — add new types without touching this
}
```

---

## Code Smells — Recognize All 22 in Interviews

### The Most Asked Smells

| Smell | Symptom | Fix |
|-------|---------|-----|
| **God Class** | `UserService` with 50 methods doing auth + email + persistence + reporting | Split by SRP |
| **Feature Envy** | Method in Class A constantly calls Class B's getters | Move method to Class B |
| **Primitive Obsession** | `String email`, `String phone`, `double price` everywhere | Create `Email`, `PhoneNumber`, `Money` value objects |
| **Long Method** | 200-line methods | Extract method, Strategy pattern |
| **Long Parameter List** | `createOrder(String id, String userId, Date d, boolean express, String coupon, ...)` | Create `OrderRequest` object |
| **Data Clumps** | `String city, String state, String zip` appear together in 5 classes | Create `Address` value object |
| **Inappropriate Intimacy** | Class A reads private fields of Class B via reflection or getters chains | Move code, add methods |
| **Shotgun Surgery** | One change requires edits to 10 different classes | Consolidate related behavior |
| **Divergent Change** | One class changes for multiple unrelated reasons | Split by SRP |
| **Temporary Field** | Instance field only set in some code paths | Extract class or use Optional |

### Code Smell Detection Exercise

```java
// HOW MANY SMELLS CAN YOU FIND?
public class UserService {
    public void processUser(String firstName, String lastName, String email,
                             String street, String city, String state, String zip,
                             boolean isPremium, double discountPercent) {
        // 200 lines...
        String fullName = firstName + " " + lastName;
        String address = street + ", " + city + ", " + state + " " + zip;
        double discount = isPremium ? discountPercent : 0;
        // sends email, updates DB, applies promo, logs audit, calls analytics...
    }
}
```

**Answer:** Long Parameter List, Data Clumps (address fields), Primitive Obsession (`double discountPercent`), God Class (doing too many things), Long Method.

---

## Practice Problems

### Easy
1. Design a `BankAccount` class that properly encapsulates balance (prevent negative balance, expose read-only view of transactions)
2. Refactor this: `class Animal { void sound() { if type=="Dog" ... else if type=="Cat" ... } }` — use polymorphism

### Medium
3. Design a `Shape` hierarchy (Circle, Rectangle, Triangle) with `area()` and `perimeter()` using sealed interfaces + records
4. Identify all OOP violations in a given Spring Boot `@Service` class (provided in mock)

### Hard
5. Design `Vehicle` hierarchy for a rental system: `Car`, `Truck`, `Motorcycle`, `ElectricCar`. Electric cars have both `Chargeable` and `Driveable` behavior. Where does inheritance vs interface vs composition fit?

---

## Mock Interview #1 Prep

**Likely questions:**
- "Explain encapsulation in the context of a real system you've built."
- "When would you use an abstract class instead of an interface in Java?"
- "Show me code that violates Liskov Substitution Principle."
- "What are 3 code smells you've found in production code?"

**Your answer framework:** Problem → Code Example → Why it breaks → Better design → Trade-off
