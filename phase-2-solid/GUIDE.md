# Phase 2 — SOLID Principles
> Week 2 | Goal: Not textbook answers — PRODUCTION explanations with real Spring Boot examples.

---

## Why Every Interviewer Asks SOLID

SOLID is a filter. Everyone gives textbook answers. The interviewer wants to see:
1. Can you identify a SOLID violation in real code?
2. Can you explain WHY it matters (not just what the rule says)?
3. Can you refactor it without breaking the world?

---

## S — Single Responsibility Principle

### The Real Definition
**"A class should have only ONE reason to change."**

Not "one class, one job" — that's too vague. Ask: "What would make me change this class?" If you can give two different answers, SRP is violated.

### The Classic Spring Boot Violation

```java
// BAD: UserService changes when:
// 1. Authentication logic changes
// 2. Email template changes
// 3. Database schema changes
// 4. Audit log format changes

@Service
public class UserService {
    public void registerUser(RegisterRequest req) {
        // Validation
        if (userRepository.existsByEmail(req.email())) {
            throw new DuplicateEmailException();
        }

        // Password hashing (auth logic)
        String hashed = BCrypt.hashpw(req.password(), BCrypt.gensalt());

        // Persistence
        User user = new User(req.email(), hashed);
        userRepository.save(user);

        // Email sending
        String body = "Welcome " + req.name() + "! Click here to verify...";
        emailClient.send(req.email(), "Welcome!", body);

        // Audit logging
        auditLog.record("USER_REGISTERED", req.email(), Instant.now());
    }
}
```

```java
// GOOD: Each class has one reason to change

@Service
public class UserRegistrationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;    // auth concern
    private final WelcomeEmailSender welcomeEmail;   // email concern
    private final UserAuditLogger auditLogger;        // audit concern

    public void register(RegisterRequest req) {
        userRepository.assertEmailNotTaken(req.email());
        User user = User.create(req.email(), passwordEncoder.encode(req.password()));
        userRepository.save(user);
        welcomeEmail.sendTo(user);
        auditLogger.logRegistration(user);
    }
}
// Now: email template changes → only WelcomeEmailSender changes
// Auth algorithm changes → only PasswordEncoder implementation changes
```

---

## O — Open/Closed Principle

### The Real Definition
**"Open for extension, closed for modification."**

When a new requirement comes in, you should be able to add it by writing NEW code, not by changing EXISTING code.

### The Classic Violation: Switch on Type

```java
// BAD: every new payment method requires modifying this method
public class PaymentProcessor {
    public void process(Payment payment) {
        switch (payment.getType()) {
            case "CREDIT_CARD" -> chargeCreditCard(payment);
            case "UPI"         -> processUPI(payment);
            case "WALLET"      -> deductWallet(payment);
            // Adding PayPal? You MUST modify this method → breaks OCP
        }
    }
}
```

```java
// GOOD: Strategy pattern = OCP in action
public interface PaymentStrategy {
    void process(Payment payment);
}

@Component("CREDIT_CARD")
public class CreditCardProcessor implements PaymentStrategy { ... }

@Component("UPI")
public class UpiProcessor implements PaymentStrategy { ... }

// Adding PayPal = just add a new class, existing code untouched
@Component("PAYPAL")
public class PayPalProcessor implements PaymentStrategy { ... }

@Service
public class PaymentProcessor {
    private final Map<String, PaymentStrategy> strategies;  // Spring injects by qualifier

    public void process(Payment payment) {
        strategies.get(payment.getType()).process(payment);
        // Never changes when new payment types are added
    }
}
```

---

## L — Liskov Substitution Principle

### The Real Definition
**"If S is a subtype of T, then objects of type T may be replaced with objects of type S without altering the correctness of the program."**

Plain English: **Subclasses must honor the contract of their parent.**

### Two Classic Traps

#### Trap 1: Square-Rectangle
(See Phase 1 OOP guide for full explanation)

#### Trap 2: Bird-Penguin

```java
// BAD: Penguin IS-A Bird, but penguin can't fly → LSP violated
class Bird {
    void fly() { System.out.println("Flying..."); }
}

class Penguin extends Bird {
    @Override
    void fly() {
        throw new UnsupportedOperationException("Penguins can't fly!");
    }
}

// Any code that calls bird.fly() will BREAK when given a Penguin
void makeBirdFly(Bird bird) {
    bird.fly();  // throws for Penguin
}
```

```java
// GOOD: Separate what varies
interface Bird { }
interface FlyingBird extends Bird { void fly(); }
interface SwimmingBird extends Bird { void swim(); }

class Eagle implements FlyingBird { ... }
class Penguin implements SwimmingBird { ... }
class Duck implements FlyingBird, SwimmingBird { ... }
```

### How to Detect LSP Violations

1. **Overridden method throws `UnsupportedOperationException`** — classic sign
2. **Subclass overrides a method to do nothing** — empty override
3. **Caller type-checks before calling** — `if (bird instanceof Penguin) skip.fly()` — LSP broken
4. **Subclass weakens a precondition** — parent requires non-null, child accepts null
5. **Subclass strengthens a postcondition** — parent guarantees returns non-empty list, child can return empty

---

## I — Interface Segregation Principle

### The Real Definition
**"Clients should not be forced to depend on interfaces they don't use."**

Fat interfaces force implementors to implement methods they don't need, leading to empty or throwing implementations.

### The Fat Interface Problem

```java
// BAD: Fat interface — PrinterScanner forces all implementors to do both
interface PrinterScanner {
    void print(Document doc);
    void scan(Document doc);
    void fax(Document doc);
}

// A basic printer that can only print is FORCED to implement scan and fax
class BasicPrinter implements PrinterScanner {
    public void print(Document doc) { /* works */ }
    public void scan(Document doc) { throw new UnsupportedOperationException(); }  // BAD
    public void fax(Document doc)  { throw new UnsupportedOperationException(); }  // BAD
}
```

```java
// GOOD: Segregated interfaces
interface Printer  { void print(Document doc); }
interface Scanner  { void scan(Document doc); }
interface Fax      { void fax(Document doc); }

class BasicPrinter implements Printer { ... }
class AdvancedMachine implements Printer, Scanner, Fax { ... }

// Caller only depends on what it needs
void printReport(Printer printer, Document doc) { printer.print(doc); }
```

### Java Standard Library Example
`Comparable<T>` vs `Comparator<T>`:
- `Comparable` = natural ordering IN the object (implements it)
- `Comparator` = external ordering strategy (segregated)

```java
// ISP in Java's own design:
// List.sort(Comparator) — caller provides ONLY the comparison logic
// Not: list.sort(new ThingThatAlsoAddsAndRemovesAndPrints())
```

---

## D — Dependency Inversion Principle

### The Real Definition
**"High-level modules should not depend on low-level modules. Both should depend on abstractions."**

Your `OrderService` (high-level business logic) should not depend directly on `MySQLOrderRepository` (low-level infrastructure). Both should depend on `OrderRepository` (abstraction).

### The Classic Spring Boot Example

```java
// BAD: High-level depends on low-level (concrete)
@Service
public class OrderService {
    private final MySQLOrderRepository orderRepo = new MySQLOrderRepository();
    // Now OrderService is COUPLED to MySQL. Switching to PostgreSQL = rewrite.
    // Testing OrderService requires a real MySQL database.
}
```

```java
// GOOD: Both depend on abstraction
public interface OrderRepository {  // abstraction
    void save(Order order);
    Optional<Order> findById(String id);
}

@Repository
public class MySQLOrderRepository implements OrderRepository { ... }

@Service
public class OrderService {
    private final OrderRepository orderRepository;  // depends on abstraction

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }
}
// Spring injects MySQLOrderRepository → DIP through Spring IoC
// Tests inject MockOrderRepository → unit testable without DB
```

### Constructor Injection vs Field Injection

```java
// Field injection — BAD for production code
@Service
public class OrderService {
    @Autowired
    private OrderRepository repo;  // hidden dependency, not testable without Spring context
}

// Constructor injection — CORRECT
@Service
public class OrderService {
    private final OrderRepository repo;

    public OrderService(OrderRepository repo) {  // Spring auto-injects since Spring 4.3
        this.repo = repo;
    }
}
```

**Why constructor injection wins:**
1. Dependencies are explicit (visible in constructor signature)
2. Object is always fully initialized (no partial construction)
3. Testable without Spring (`new OrderService(mockRepo)`)
4. `final` fields — immutable, thread-safe

---

## SOLID Violations Quick Reference

| Violation | Tell-tale Sign | Fix |
|-----------|----------------|-----|
| SRP | Class name ends in "Manager", "Helper", "Utils" | Split into focused classes |
| OCP | `if/else` or `switch` on type | Strategy or Factory pattern |
| LSP | `throws UnsupportedOperationException` in override | Redesign hierarchy |
| ISP | Interface with 10+ methods | Split into smaller interfaces |
| DIP | `new ConcreteImplementation()` in business class | Inject abstraction |

---

## Practice Problems

### Easy
1. `UserService` has methods: `authenticate()`, `sendWelcomeEmail()`, `generateReport()`. Apply SRP.
2. Identify which SOLID principle is violated: `List<Employee> getAllEmployees()` returns a mutable list that callers modify.

### Medium
3. Refactor a payment switch-case to use Strategy + Factory (OCP + DIP).
4. Design a notification system: `Notifier` interface that supports email, SMS, push — but some channels don't support all types (ISP).

### Hard
5. You have `OrderService` that uses `OrderRepository`, `InventoryRepository`, `EmailSender`, and `PaymentGateway`. Which dependencies should be interfaces? Which can be concrete? Justify each.

---

## Mock Interview #1 — SOLID Round (30 min)

You will be shown code. Your job:
1. Identify which SOLID principles are violated
2. Explain WHY it's a violation (not just the name)
3. Propose refactoring
4. Discuss the trade-off (what complexity does the fix add?)
