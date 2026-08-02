# OCP — Open/Closed Principle
> Personal reference: definition + tree architecture + interview answer

---

## Definition

> "Software entities should be open for extension, but closed for modification."

### Plain English Version

> **"When a new requirement comes in, add NEW code — don't change EXISTING code."**

If you have to open an existing method and add an `if/else` branch for every new case —
that class is not closed for modification. OCP is violated.

### One-Line Memory Hook

> **"Add a new class, not a new if/else."**

---

## Problem 1 — DiscountService

### BEFORE — Violation

```
DiscountService.calculateDiscount()
│
├── if "PREMIUM"       → 20% discount
├── else if "STUDENT"  → 10% discount
├── else if "EMPLOYEE" → 30% discount
│
│   ← every new customer type = MODIFY this method
│
├── (next sprint) else if "SENIOR_CITIZEN" → 15%  ← modify again ❌
├── (Q3)          else if "VETERAN"        → 25%  ← modify again ❌
└── (Q4)          else if "AFFILIATE"      → 12%  ← modify again ❌

Risk: each modification can accidentally break existing discount logic.
      PREMIUM branch touched when adding SENIOR_CITIZEN → regression possible.
```

**Why OCP is violated:**
- Every new customer type forces modification of the existing `calculateDiscount()` method
- Existing working code is touched — risk of regression
- The method grows unboundedly: 3 types today → 10 types next year

---

### AFTER — Fixed (Strategy Pattern)

```
DiscountStrategy  (interface — the abstraction)
│   └── BigDecimal apply(Order order)
│
├── PremiumStrategy       @Component("PREMIUM")        → 20% ✅
├── StudentStrategy       @Component("STUDENT")        → 10% ✅
├── EmployeeStrategy      @Component("EMPLOYEE")       → 30% ✅
├── SeniorCitizenStrategy @Component("SENIOR_CITIZEN") → 15% ✅
│
│   ← new type next sprint? Just add a new class ↓
│
└── VeteranStrategy       @Component("VETERAN")        → 25% ✅ (zero changes elsewhere)


DiscountServiceOrchestrator
│
└── Map<String, DiscountStrategy> strategies
        │
        ├── key: "PREMIUM"        → value: PremiumStrategy instance
        ├── key: "STUDENT"        → value: StudentStrategy instance
        ├── key: "EMPLOYEE"       → value: EmployeeStrategy instance
        └── key: "SENIOR_CITIZEN" → value: SeniorCitizenStrategy instance

calculateDiscounts(order, customerType):
    strategy = strategies.get(customerType)  ← single map lookup
    return strategy.apply(order)              ← single dispatch
    ← THIS METHOD NEVER CHANGES
```

---

### How Spring Builds the Map Automatically

```
@Component("PREMIUM")        ← bean name = map key
public class PremiumStrategy implements DiscountStrategy { ... }

@Component("STUDENT")
public class StudentStrategy implements DiscountStrategy { ... }

Spring scans at startup:
    Finds all beans implementing DiscountStrategy
    Builds: Map<String, DiscountStrategy> automatically
    Injects into DiscountServiceOrchestrator constructor

Adding VETERAN? Write ONE new @Component class.
Spring adds it to the map. DiscountServiceOrchestrator never changes.
```

---

### Your Driver.java — Manual Wiring (without Spring)

```java
// Driver.java shows the same pattern without Spring auto-wiring
// Manually builds the same map Spring would inject

strategyMap.put("PREMIUM",        new PremiumStrategy());
strategyMap.put("STUDENT",        new StudentStrategy());
strategyMap.put("SENIOR_CITIZEN", new SeniorCitizenStrategy());
strategyMap.put("EMPLOYEE",       new EmployeeStrategy());

DiscountServiceOrchestrator orchestrator = new DiscountServiceOrchestrator(strategyMap);
orchestrator.calculateDiscounts(order, "PREMIUM");  // → 2.00 (10 * 20%)
```

---

### The Key Shift

```
BEFORE                              AFTER
────────────────────────────────────────────────────────────
calculateDiscount()                 DiscountServiceOrchestrator
    if PREMIUM   → 20%                  └── Map.get(type).apply(order)
    if STUDENT   → 10%                       ← never changes
    if EMPLOYEE  → 30%
    if ???       → modify again         DiscountStrategy (interface)
                                            ├── PremiumStrategy       ✅
Open for modification ❌                ├── StudentStrategy       ✅
Closed for extension  ❌                ├── EmployeeStrategy      ✅
                                        └── SeniorCitizenStrategy ✅
                                            + VeteranStrategy      ← just add new class

Closed for modification ✅
Open for extension      ✅
```

---

### The OCP Test

> "Can I add a new `VETERAN` discount type without touching `DiscountServiceOrchestrator`?"

```
BEFORE:  No → must open and modify calculateDiscount() ❌
AFTER:   Yes → just add VeteranStrategy @Component("VETERAN") ✅
```

If yes → OCP satisfied.
If no  → OCP violated.

---

### Code Structure (your implementation)

```
ocp/problem1/
├── DiscountService.java                     ← VIOLATION: if/else growing forever
├── Driver.java                              ← shows manual map wiring ✅
│
└── fix/strategy/
    ├── DiscountStrategy.java                ← interface: BigDecimal apply(Order) ✅
    ├── DiscountServiceOrchestrator.java     ← Map dispatch, never changes ✅
    └── impl/
        ├── PremiumStrategy.java             ← @Component("PREMIUM")  20% ✅
        ├── StudentStrategy.java             ← @Component("STUDENT")  10% ✅
        ├── EmployeeStrategy.java            ← @Component("EMPLOYEE") 30% ✅
        └── SeniorCitizenStrategy.java       ← @Component("SENIOR_CITIZEN") 15% ✅
```

---

### ⚠️ One Comment Bug to Fix

All strategy classes have copy-pasted comment `"Premium customers get a 20% discount"`.
`StudentStrategy`, `EmployeeStrategy`, `SeniorCitizenStrategy` have wrong comments.
Update each to reflect the actual discount:

```java
// PremiumStrategy
// Premium customers get 20% discount

// StudentStrategy
// Student customers get 10% discount

// EmployeeStrategy
// Employee customers get 30% discount

// SeniorCitizenStrategy
// Senior citizen customers get 15% discount
```

---

## How to Detect OCP Violations (Quick Reference)

| Tell-tale Sign | Example | Verdict |
|---|---|---|
| `if/else` or `switch` on a type string | `if (type.equals("CREDIT_CARD"))` in PaymentProcessor | ❌ OCP violated |
| Method comment says "add new case here" | `// Adding PayPal? Modify here` | ❌ OCP violated |
| Adding a new variant requires modifying existing code | New payment type = touch existing method | ❌ OCP violated |
| Strategy pattern with map dispatch | `strategies.get(type).process(payment)` | ✅ OCP satisfied |
| New variant = new class only | Add `@Component("PAYPAL") class PayPalStrategy` | ✅ OCP satisfied |

---

## Interview Answer Template

> "OCP says a class should be open for extension but closed for modification.
> When you need to add new behavior, you should write new code — not change existing code.
>
> The classic violation is an if/else chain that grows with every new requirement.
> In my code, `DiscountService.calculateDiscount()` had a branch for each customer type.
> Every new type required opening and modifying that method — risking regression on
> existing logic.
>
> The fix was the Strategy pattern. I defined a `DiscountStrategy` interface with
> `apply(Order order)`. Each customer type becomes a `@Component` class.
> `DiscountServiceOrchestrator` holds a `Map<String, DiscountStrategy>` injected
> by Spring — it does a single `map.get(type).apply(order)` and never changes.
> Adding a new customer type is just writing one new class. Zero lines changed anywhere else.
>
> Proof of OCP: I added `SeniorCitizenStrategy` without touching `DiscountServiceOrchestrator`."

---

## SOLID Quick Recap (where OCP fits)

```
S — Single Responsibility  →  One reason to change
O — Open/Closed            →  Open for extension, closed for modification  ← YOU ARE HERE
L — Liskov Substitution    →  Subtypes must honor parent's contract
I — Interface Segregation  →  Don't force implementors to use unused methods
D — Dependency Inversion   →  Depend on abstractions, not concretions
```

---

## Your Reference Files So Far

```
phase-2-solid/
├── srp/SRP-REFERENCE.md   ← Single Responsibility Principle
├── ocp/OCP-REFERENCE.md   ← Open/Closed Principle  ← THIS FILE
├── lsp/LSP-REFERENCE.md   ← Liskov Substitution Principle
├── isp/ISP-REFERENCE.md   ← Interface Segregation Principle
└── dip/DIP-REFERENCE.md   ← Dependency Inversion Principle
```
