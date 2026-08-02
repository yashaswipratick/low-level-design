# DIP — Dependency Inversion Principle
> Personal reference: definition + tree architecture + interview answer

---

## Definition

> "High-level modules should not depend on low-level modules. Both should depend on abstractions."
> "Abstractions should not depend on details. Details should depend on abstractions."

### Plain English Version

> **"Your business logic should not know which specific tool it uses. It should only know what the tool can do."**

### One-Line Memory Hook

> **"Depend on what it does, not on what it is."**

`ElectricSwitch` should know a device can turn on/off — not that it's specifically a `LightBulb`.
`OrderService` should know it can send an email — not that it's specifically `SendGridEmailClient`.

---

## Problem 4 — ElectricSwitch / LightBulb (Simple Example)

### BEFORE — Violation (`without_fix`)

```
ElectricSwitch  (high-level)
    │
    └── new LightBulb()   ← hardwired to concrete class
            ├── turnOn()
            └── turnOff()

Problem:
    Want to control a Fan?    → Modify ElectricSwitch ❌
    Want to control AirCon?   → Modify ElectricSwitch ❌
    Want to unit test?        → Can't mock LightBulb ❌
    Switch is MARRIED to Bulb → tightly coupled ❌
```

**Why DIP is violated:**
- `ElectricSwitch` (high-level) directly depends on `LightBulb` (low-level concrete)
- Adding any new device requires modifying `ElectricSwitch`
- `private LightBulb bulb = new LightBulb()` — the keyword `new` is the red flag

---

### AFTER — Fixed (`fix`)

```
          Switchable  (abstraction — the contract)
          │   ├── turnOn()
          │   └── turnOff()
          │
          ├── LightBulb implements Switchable ✅
          │       ├── turnOn()  → "LightBulb ON"
          │       └── turnOff() → "LightBulb OFF"
          │
          ├── Fan implements Switchable ✅
          │       ├── turnOn()  → "Fan Spinning"
          │       └── turnOff() → "Fan Stopped"
          │
          └── AirCon implements Switchable ✅  ← new device, zero changes to Switch
                  ├── turnOn()  → "AirCon cooling"
                  └── turnOff() → "AirCon off"

ElectricSwitch  (high-level)
    │
    └── Switchable  ← depends on abstraction, not concrete
            │
            └── injected via constructor (never new-ed inside)


Usage:
    new ElectricSwitch(new LightBulb())  ✅
    new ElectricSwitch(new Fan())        ✅
    new ElectricSwitch(new AirCon())     ✅  ← ElectricSwitch never changed
```

---

### The Key Shift

```
BEFORE                              AFTER
────────────────────────────────────────────────────────────
ElectricSwitch                      ElectricSwitch
    │                                   │
    └── new LightBulb() ← concrete      └── Switchable ← abstraction
                                                ├── LightBulb ✅
    Want Fan? Modify Switch ❌              ├── Fan ✅
    Want AirCon? Modify Switch ❌           └── AirCon ✅

Coupled to implementation           Coupled to abstraction only
Tested only with real LightBulb     Tested with any mock Switchable
```

---

### The 3 Signals That DIP Is Satisfied

```java
// Signal 1: field type is the INTERFACE, not the concrete class
private final Switchable switchable;         // ✅ interface type

// Signal 2: dependency is INJECTED, not new-ed inside
public ElectricSwitch(Switchable switchable) {  // ✅ constructor injection
    this.switchable = switchable;
}

// Signal 3: field is FINAL — immutable, set once, thread-safe
private final Switchable switchable;         // ✅ final
```

If you see `new ConcreteClass()` inside a high-level class → DIP is violated.

---

### Code Structure (your implementation)

```
dip/problem4/
├── without_fix/
│   ├── LightBulb.java        ← concrete low-level class
│   └── ElectricSwitch.java   ← VIOLATION: new LightBulb() hardwired
│
└── fix/
    ├── Switchable.java       ← abstraction (the contract both sides depend on)
    ├── LightBulb.java        ← implements Switchable ✅
    ├── Fan.java              ← implements Switchable ✅
    └── ElectricSwitch.java   ← depends on Switchable, injected via constructor ✅
```

---

## Real Spring Boot DIP Example

```
BEFORE (violation)                       AFTER (fixed)
──────────────────────────────────────────────────────────────────
OrderService                             OrderService
    │                                        │
    ├── new SendGridEmailClient(apiKey)       ├── EmailSender  (interface)
    │   (hardwired vendor + credentials)     │       └── SendGridEmailSender @Component
    │                                        │
    └── new TwilioSmsClient(sid, token)      └── SmsSender    (interface)
        (hardwired vendor + credentials)             └── TwilioSmsSender @Component


OrderService constructor:
BEFORE: OrderService(OrderRepository repo)       ← hides email/sms deps
AFTER:  OrderService(OrderRepository repo,       ← explicit, all deps visible
                     EmailSender email,
                     SmsSender sms)
```

---

## How DIP Connects to the Other Principles

```
DIP says: depend on abstractions

This directly enables:
    OCP  → you can extend by adding new implementations, never modifying high-level class
           (ElectricSwitch never changes when new devices are added)

    LSP  → since you depend on interface, any valid implementation can substitute
           (any Switchable can replace any other Switchable in ElectricSwitch)

    ISP  → you define small focused interfaces → DIP makes you inject only what you need
           (ElectricSwitch only needs Switchable — not a fat DeviceManager)

DIP is the glue that holds O, L, I together.
```

---

## How to Detect DIP Violations (Quick Reference)

| Tell-tale Sign | Example | Verdict |
|---|---|---|
| `new ConcreteClass()` inside a high-level class | `new SendGridEmailClient(key)` inside `OrderService` | ❌ DIP violated |
| `@Autowired` on a private field | `@Autowired private EmailService emailService` | ❌ DIP violated |
| Field type is a concrete class | `private LightBulb bulb` | ❌ DIP violated |
| Credentials / config hardcoded in source | `new TwilioClient("AC-hardcoded", "token")` | ❌ DIP violated |
| Unit test requires real external service | Can't test without real SendGrid / DB | ❌ DIP violated |
| Constructor accepts interface types | `OrderService(EmailSender e, SmsSender s)` | ✅ DIP satisfied |
| Field is `final` + interface type | `private final Switchable device` | ✅ DIP satisfied |

---

## Interview Answer Template

> "DIP says high-level modules should not depend on low-level modules — both should
> depend on abstractions.
>
> The classic violation is using `new ConcreteClass()` inside business logic.
> In my code, `ElectricSwitch` had `new LightBulb()` hardwired inside it. Adding a Fan
> required modifying the Switch — a high-level policy class changed because of a
> low-level device decision.
>
> The fix was to introduce a `Switchable` interface. Both `ElectricSwitch` and `LightBulb`
> now depend on that abstraction. The switch accepts any `Switchable` via constructor
> injection — `LightBulb`, `Fan`, `AirCon` — without a single line change.
>
> In Spring Boot, this is exactly what constructor injection + interfaces give you.
> `OrderService` depending on `EmailSender` (interface) rather than `SendGridEmailClient`
> (concrete) means you can swap providers via config, and unit test with a mock — no
> real HTTP calls, no API keys needed."

---

## SOLID Quick Recap (where DIP fits)

```
S — Single Responsibility  →  One reason to change
O — Open/Closed            →  Open for extension, closed for modification
L — Liskov Substitution    →  Subtypes must honor parent's contract
I — Interface Segregation  →  Don't force implementors to use unused methods
D — Dependency Inversion   →  Depend on abstractions, not concretions  ← YOU ARE HERE
```

---

## The `new` Keyword Is the Red Flag

```java
// Every time you see this inside a high-level class — ask:
// "What if I want to swap this implementation?"

private LightBulb    bulb    = new LightBulb();         // ❌ hardwired
private SendGridClient email = new SendGridClient(key);  // ❌ hardwired
private MixpanelClient track = new MixpanelClient(tok);  // ❌ hardwired

// The fix is always the same:
// 1. Extract an interface
// 2. Inject via constructor
// 3. Make the field final

private final Switchable    device;   // ✅
private final EmailSender   email;    // ✅
private final AnalyticsTracker track; // ✅
```

---

## Your Reference Files So Far

```
phase-2-solid/
├── lsp/LSP-REFERENCE.md   ← Liskov Substitution Principle
├── isp/ISP-REFERENCE.md   ← Interface Segregation Principle
└── dip/DIP-REFERENCE.md   ← Dependency Inversion Principle  ← THIS FILE
```
