# Phase 3 — Clean Code
> Week 5 | Goal: Code that reads like prose. Every line justified.

---

## Why Clean Code is an Interview Signal

Interviewers don't just evaluate your design — they evaluate how you write code live. Poorly named variables, giant methods, and confusing logic say "this engineer doesn't think about maintainability." Clean code in a live session signals seniority.

---

## Naming — The Most Underrated Skill

### The Rules

| Rule | Bad | Good |
|------|-----|------|
| Reveal intent | `int d` | `int daysSinceLastOrder` |
| No type in names | `List<User> userList` | `List<User> users` |
| No abbreviations | `calcTot()` | `calculateOrderTotal()` |
| Use domain language | `processStuff()` | `fulfillOrder()` |
| Boolean names as predicates | `boolean flag` | `boolean isEligibleForDiscount` |
| Method names as verbs | `discount()` | `applyDiscount()` |

### The Newspaper Test for Classes

Read just the class name. Can you guess what it does? If not, rename it.

```java
// Fails newspaper test
class DataHandler { }
class Manager { }
class Processor { }

// Passes newspaper test
class OrderFulfillmentService { }
class LowStockAlertPipeline { }
class CartPricingCalculator { }
```

---

## Methods — Small, Single-Level Abstraction

### The One Rule That Changes Everything

**A method should do ONE thing, and do it well, and do it only.**

How do you know if it does one thing? If you can extract a piece of it into a new method with a name that's NOT just restating the method body, it was doing more than one thing.

### The Abstraction Level Principle

All statements in a method should be at the same level of abstraction.

```java
// BAD: Mixed levels of abstraction
public void placeOrder(Order order) {
    // High level
    validateOrder(order);

    // Low level — shouldn't be here
    String sql = "INSERT INTO orders (id, total) VALUES (?, ?)";
    PreparedStatement stmt = connection.prepareStatement(sql);
    stmt.setString(1, order.getId());
    stmt.setDouble(2, order.getTotal());
    stmt.execute();

    // High level again
    notifyCustomer(order);
}
```

```java
// GOOD: Single level of abstraction — reads like a story
public void placeOrder(Order order) {
    validateOrder(order);
    persistOrder(order);      // hides the SQL details
    notifyCustomer(order);
}

private void persistOrder(Order order) {
    orderRepository.save(order);
}
```

### Method Length

Rule: If a method needs a comment to explain what a block of code does, extract that block into a method with that comment as its name.

```java
// BAD: Comment explains a block → extract it
public void processOrder(Order order) {
    // Validate the order
    if (order == null) throw new NullPointerException();
    if (order.getItems().isEmpty()) throw new EmptyOrderException();
    if (order.getTotal().isNegative()) throw new InvalidAmountException();

    // Apply discounts
    if (order.getCustomer().isPremium()) {
        order.applyDiscount(0.1);
    }
    // ...20 more lines
}

// GOOD: Comments become method names
public void processOrder(Order order) {
    validateOrder(order);
    applyEligibleDiscounts(order);
    // ...
}
```

---

## Classes — Clean OOP Structure

### The Three-Clause Class Rule

1. Class name is a noun (not a verb, not vague like "Manager")
2. All fields relate to what the class IS
3. All methods relate to what the class DOES

### The Rule of 30 / 300

Guideline (not law):
- Method: ≤30 lines
- Class: ≤300 lines

When you exceed these, ask: "Am I doing too much?"

### Don't Use Comments to Explain BAD Code

```java
// BAD: Comment compensates for bad naming
int d; // days since last order

// GOOD: No comment needed
int daysSinceLastOrder;
```

```java
// BAD: Comment explains WHAT — code should be self-explanatory
// Check if user is premium and order total exceeds 1000
if (u.isPrem() && o.getAmt() > 1000) { ... }

// GOOD: No comment needed — reads itself
if (customer.isPremiumMember() && order.total().exceeds(Money.of(1000))) { ... }
```

**Comments are for WHY, never for WHAT.**

```java
// GOOD: Comment explains WHY — this is non-obvious
// We use exponential backoff starting at 100ms because the payment provider
// has a known rate limit of 10 retries/second per merchant ID
Thread.sleep(100 * (long) Math.pow(2, attemptNumber));
```

---

## Error Handling

### Don't Return null — Use Optional

```java
// BAD: forces every caller to null-check
public User findByEmail(String email) {
    User user = userRepository.findByEmail(email);
    return user;  // might be null — caller doesn't know
}

// GOOD: Optional makes absent value explicit
public Optional<User> findByEmail(String email) {
    return userRepository.findByEmail(email);
}

// Caller:
findByEmail(email)
    .ifPresentOrElse(
        user -> processUser(user),
        () -> { throw new UserNotFoundException(email); }
    );
```

### Don't Return error codes — Use Exceptions

```java
// BAD: caller ignores return code
int result = placeOrder(order);  // is 0 success or error?

// GOOD: exception forces caller to handle failure
void placeOrder(Order order) throws InsufficientInventoryException { ... }
```

### Wrap Third-Party Exceptions

```java
// BAD: callers depend on Stripe's exception class
try {
    stripeClient.charge(amount);
} catch (StripeException e) {  // leaks Stripe into your domain
    throw e;
}

// GOOD: wrap in domain exception
try {
    stripeClient.charge(amount);
} catch (StripeException e) {
    throw new PaymentGatewayException("Stripe charge failed", e);
}
```

---

## Refactoring Legacy Code — The Process

```
1. Don't touch anything without tests first
2. Identify the smell (use the taxonomy from Phase 1)
3. Choose the refactoring technique:
   - Extract Method
   - Extract Class
   - Rename
   - Replace Conditional with Polymorphism
   - Introduce Parameter Object
   - Replace Magic Number with Named Constant
4. Apply in the smallest possible step
5. Verify tests still pass
6. Repeat
```

### Replace Magic Numbers

```java
// BAD
if (order.getAge() > 30) { applyLateFee(); }

// GOOD
private static final int MAX_RETURN_WINDOW_DAYS = 30;
if (order.getAge() > MAX_RETURN_WINDOW_DAYS) { applyLateFee(); }
```

### Introduce Parameter Object

```java
// BAD: Long Parameter List smell
public Order createOrder(String customerId, String productId, int qty,
                          String coupon, String address, String paymentMethod) { ... }

// GOOD
public record CreateOrderRequest(
    String customerId,
    String productId,
    int quantity,
    Optional<String> couponCode,
    Address deliveryAddress,
    PaymentMethod paymentMethod
) {}

public Order createOrder(CreateOrderRequest request) { ... }
```

---

## Practice Problems

### Easy
1. Rename all variables in this snippet to reveal intent: `int x = 86400; List<String> lst; boolean b;`
2. Extract 3 methods from this 60-line `process()` method (provided).

### Medium
3. Take a Spring Boot controller with business logic embedded in it — extract to Service, Repository layers with clean naming.
4. Replace all null returns in a given service with `Optional<>`.

### Hard
5. Take a real God Class from your experience. Identify all smells. Propose the full refactoring plan (which classes to create, what each does, which patterns to apply).
