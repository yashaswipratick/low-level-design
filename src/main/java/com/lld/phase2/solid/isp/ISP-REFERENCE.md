# ISP — Interface Segregation Principle
> Personal reference: definition + tree architecture + interview answer

---

## Definition

> "Clients should not be forced to depend on interfaces they don't use."

### Plain English Version

> **"Don't make a class implement methods it has no use for."**

A fat interface forces every implementor to sign up for capabilities they may not have.
The result: empty stubs, throws, and lies in the codebase.

### One-Line Memory Hook

> **"Sign only what you can deliver."**

---

## Problem 4 — Printer / Scanner / Fax

### BEFORE — Violation (`without_fix`)

```
Printer  (fat interface — 3 promises)
│   ├── print(doc)     ← capability 1
│   ├── scan(doc)      ← capability 2
│   └── fax(doc)       ← capability 3
│
├── BasicPrinter  (can only print)
│       ├── print()  ✅ works
│       ├── scan()   💥 throws UnsupportedOperationException
│       └── fax()    💥 throws UnsupportedOperationException
│
└── AdvancedPrinter  (can do everything)
        ├── print()  ✅ works
        ├── scan()   ✅ works
        └── fax()    ✅ works


Caller:
void process(Printer p, Document doc) {
    p.scan(doc);   ← 💥 crashes at RUNTIME if p = BasicPrinter
}
```

**Why ISP is violated:**
- `BasicPrinter` is forced to implement `scan()` and `fax()` it cannot perform
- The interface promises 3 capabilities — `BasicPrinter` can only keep 1
- Any caller that uses `Printer` and calls `scan()` crashes at runtime

---

### AFTER — Fixed (`fix`)

```
Printer              Scanner              Fax
(1 promise)          (1 promise)          (1 promise)
│  └── print()       │  └── scan()        │  └── fax()
│                    │                    │
│                    └────────────────────┘
│                             │
│                      AdvancedPrinter
│                      implements all 3 ✅
│                          ├── print()
│                          ├── scan()
│                          └── fax()
│
└── BasicPrinter
    implements Printer only ✅
        └── print()         (no throws, no stubs)


Callers:

printDocument(Printer p)       ← accepts BasicPrinter ✅, AdvancedPrinter ✅
scanDocument(Scanner s)        ← REJECTS BasicPrinter ✅ (compile error)
sendFax(Fax f)                 ← REJECTS BasicPrinter ✅ (compile error)
```

---

### The Key Shift

```
BEFORE                              AFTER
────────────────────────────────────────────────────────────
One fat interface                   Three focused interfaces
    │                                   │         │       │
    ├── BasicPrinter                Printer   Scanner   Fax
    │       ├── print()  ✅             │              │
    │       ├── scan()   💥         BasicPrinter  AdvancedPrinter
    │       └── fax()    💥         (print only)  (print+scan+fax)
    │
    └── AdvancedPrinter ✅          No throws ✅   All methods ✅

Bug caught at:  RUNTIME             Bug caught at:  COMPILE TIME
```

---

### Promise Table

```
Promise made by interface  →  Must be kept by every implementor
─────────────────────────────────────────────────────────────────
Printer
    print()   →  BasicPrinter ✅  |  AdvancedPrinter ✅

Scanner
    scan()    →  BasicPrinter ✗ (not a Scanner — compiler prevents)
                 AdvancedPrinter ✅

Fax
    fax()     →  BasicPrinter ✗ (not a Fax — compiler prevents)
                 AdvancedPrinter ✅
```

Every implementor keeps **100% of the promises it signed up for** — ISP satisfied.

---

### Code Structure (your implementation)

```
isp/problem4/
├── without_fix/
│   ├── Printer.java          ← VIOLATION: fat interface (print + scan + fax)
│   └── BasicPrinter.java     ← VIOLATION: throws on scan() and fax()
│
└── fix/
    ├── Printer.java          ← only print()
    ├── Scanner.java          ← only scan()
    ├── Fax.java              ← only fax()
    └── impl/
        ├── BasicPrinter.java     ← implements Printer only ✅
        └── AdvancedPrinter.java  ← implements Printer, Scanner, Fax ✅
```

---

## How ISP Connects to LSP

These two principles are siblings. Violating ISP almost always leads to an LSP violation:

```
ISP violation                     →   LSP violation
─────────────────────────────────────────────────────
Fat interface forces               Subtype cannot honor
implementor to sign up             the full contract
for unused methods                      │
        │                               ↓
        ↓                       throws UnsupportedOperationException
throws UnsupportedOperationException    │
        │                               ↓
        ↓                       Substituting subtype for parent
Runtime crash                   breaks the program
```

**Fix both at once:** Split the interface → each implementor signs only what it can honor → LSP automatically satisfied.

---

## How to Detect ISP Violations (Quick Reference)

| Tell-tale Sign | Example | Verdict |
|---|---|---|
| Interface has 8+ unrelated methods | `UserRepository` with analytics + admin + read + write | ❌ ISP violated |
| Implementor throws on some methods | `scan() { throw new UnsupportedOperationException(); }` | ❌ ISP violated |
| Implementor has empty no-op methods | `fax() { /* not supported */ }` | ❌ ISP violated |
| Test mocks 9 methods to test 1 | Mock AuthService for JwtAuthFilter | ❌ ISP violated |
| Every implementor uses every method | All methods implemented meaningfully | ✅ ISP satisfied |

---

## Real-World Spring Boot Example

```
BEFORE (fat AuthService — 9 methods)           AFTER (3 focused interfaces)
─────────────────────────────────────────────────────────────────────────────
AuthService                                TokenService
    ├── login()                                ├── generate()
    ├── logout()                               ├── validate()
    ├── validateToken()     ← used by filter   └── invalidate()
    ├── resetPassword()
    ├── changePassword()                    CredentialService
    ├── enable2FA()                             ├── authenticate()
    ├── disable2FA()                            ├── resetPassword()
    ├── generate2FACode()                       └── changePassword()
    └── verify2FACode()
                                            TwoFactorService
JwtAuthFilter                                   ├── enable()
    depends on → AuthService (9 methods)        ├── disable()
    uses only  → validateToken() (1 method)     ├── generateCode()
    test mock  → stub 8 unused methods 😩       └── verifyCode()

                                            JwtAuthFilter
                                                depends on → TokenService (3 methods)
                                                uses       → validate() (1 method)
                                                test mock  → stub 2 unused methods ✅
```

---

## Interview Answer Template

> "ISP says clients should not be forced to depend on interfaces they don't use.
>
> The classic violation is a fat interface where some implementors throw
> `UnsupportedOperationException` on certain methods — they signed a contract
> they couldn't honor.
>
> In my code, `BasicPrinter` implemented a `Printer` interface that included
> `scan()` and `fax()`. Since `BasicPrinter` can only print, it had to throw
> on those two methods. Any caller using the `Printer` interface that called
> `scan()` would crash at runtime.
>
> The fix was to split into three focused interfaces: `Printer`, `Scanner`, `Fax`.
> `BasicPrinter` now implements only `Printer` — it makes exactly one promise and
> keeps it. `AdvancedPrinter` implements all three. The compiler now prevents
> a `BasicPrinter` from being passed anywhere a `Scanner` is expected.
> Bug moved from runtime crash → compile-time error."

---

## SOLID Quick Recap (where ISP fits)

```
S — Single Responsibility  →  One reason to change
O — Open/Closed            →  Open for extension, closed for modification
L — Liskov Substitution    →  Subtypes must honor parent's contract
I — Interface Segregation  →  Don't force implementors to use unused methods  ← YOU ARE HERE
D — Dependency Inversion   →  Depend on abstractions, not concretions
```

---

## ISP vs LSP — Key Difference

```
ISP asks: "Does the INTERFACE have too many methods for some implementors?"
LSP asks: "Does the IMPLEMENTOR honor every method it promised?"

They often appear together:
    Fat interface (ISP violation)
        → Implementor can't fulfill all methods
            → Implementor throws or stubs (LSP violation)

Fix ISP → LSP violations caused by fat interfaces disappear automatically.
```
