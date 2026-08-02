# LSP — Liskov Substitution Principle
> Personal reference: definition + tree architecture + interview answer

---

## Definition

> "If S is a subtype of T, then objects of type T may be replaced with objects of type S without altering the correctness of the program."

### Plain English Version

> **"Wherever I use the parent, I should be able to plug in the child and the program still works correctly."**

### One-Line Memory Hook

> **"Don't make a promise you can't keep."**

If a class says `implements SomeInterface`, it is making a **legal promise** to every caller.
LSP says: every method in that interface must work correctly — no throws, no empty stubs, no wrong behavior.

---

## Problem 1 — ReadOnlyUserCache

### BEFORE — Violation

```
UserRepository  (interface — 5 promises)
│   ├── findById()
│   ├── findAll()
│   ├── findByEmail()
│   ├── save()          ← problem
│   └── delete()        ← problem
│
├── JpaUserRepository   ✅ keeps all 5 promises
│
└── ReadOnlyUserCache   ❌ breaks 2 promises
        ├── findById()      → works
        ├── findAll()       → works
        ├── findByEmail()   → works
        ├── save()          → 💥 throws UnsupportedOperationException
        └── delete()        → 💥 throws UnsupportedOperationException


Caller:
updateUser(UserRepository repo)  ← accepts ANY UserRepository
    repo.save(user)              ← 💥 crashes at RUNTIME if repo = ReadOnlyUserCache
```

**Why LSP is violated:**
- T = `UserRepository`, S = `ReadOnlyUserCache`
- Substituting S for T in `updateUser()` crashes the program
- The child signed the contract but could not keep 2 of 5 promises

---

### AFTER — Fixed

```
ReadableUserRepository          WritableUserRepository
(interface — 3 promises)        (interface — 2 promises)
│   ├── findById()              │   ├── save()
│   ├── findAll()               │   └── delete()
│   └── findByEmail()           │
│                               │
│         UserRepository        │
│         (extends both)        │
│         └──────────────────── ┘
│                    │
│                    └── JpaUserRepository  ✅ keeps all 5 promises
│
└── ReadOnlyUserCache  ✅ keeps all 3 promises (never signs write contract)
        ├── findById()      → works
        ├── findAll()       → works
        └── findByEmail()   → works


Callers:

UserProfileService
    depends on → ReadableUserRepository
    accepts    → ReadOnlyUserCache   ✅ (honors 3/3)
    accepts    → JpaUserRepository   ✅ (honors 3/3 + more)

UserRegistrationService
    depends on → WritableUserRepository
    accepts    → JpaUserRepository   ✅ (honors 2/2)
    REJECTS    → ReadOnlyUserCache   ✅ (COMPILE ERROR — never reaches runtime)
```

---

### The Key Shift

```
BEFORE                              AFTER
────────────────────────────────────────────────────────────
One fat contract                    Two focused contracts
    │                                   │               │
    ├── JpaUserRepository           ReadOnly        Writable
    └── ReadOnlyUserCache               │               │
            │                       ReadOnly        JpaRepo
            ├── reads:  ✅           Cache ✅            ✅
            ├── save:   💥
            └── delete: 💥

Bug caught at:  RUNTIME             Bug caught at:  COMPILE TIME
```

---

### Promise Table

```
Promise made by interface  →  Must be kept by every implementor
─────────────────────────────────────────────────────────────────
ReadableUserRepository
    findById()    →  ReadOnlyUserCache ✅  |  JpaUserRepository ✅
    findAll()     →  ReadOnlyUserCache ✅  |  JpaUserRepository ✅
    findByEmail() →  ReadOnlyUserCache ✅  |  JpaUserRepository ✅

WritableUserRepository
    save()        →  ReadOnlyUserCache ✗ (not a WritableUserRepository — compiler prevents)
                     JpaUserRepository ✅
    delete()      →  ReadOnlyUserCache ✗ (compiler prevents)
                     JpaUserRepository ✅
```

Every implementor keeps **100% of the promises it signed up for** — LSP satisfied.

---

### Code Structure (your implementation)

```
lsp/problem1/
├── UserRepository.java                  ← VIOLATION: fat interface (5 promises)
└── ReadOnlyUserCache.java               ← VIOLATION: breaks 2 promises

lsp/problem1/fix/
├── ReadableUserRepository.java          ← 3 read promises
├── WritableUserRepository.java          ← 2 write promises
├── UserRepository.java                  ← extends both (composed, empty body)
└── impl/
    ├── ReadOnlyUserCache.java           ← implements ReadableUserRepository only ✅
    ├── JpaUserRepository.java           ← implements UserRepository (all 5) ✅
    ├── UserProfileService.java          ← depends on ReadableUserRepository ✅
    └── UserRegistrationService.java     ← depends on WritableUserRepository ✅
```

---

## How to Detect LSP Violations (Quick Reference)

| Tell-tale Sign | Example | Verdict |
|---|---|---|
| Override throws `UnsupportedOperationException` | `save() { throw new UnsupportedOperationException(); }` | ❌ LSP violated |
| Override is an empty no-op | `fly() { /* penguins can't fly */ }` | ❌ LSP violated |
| Caller does `instanceof` check before calling | `if (repo instanceof ReadOnlyCache) skip.save()` | ❌ LSP violated |
| Subclass weakens postcondition | Parent returns non-empty list; child returns empty | ❌ LSP violated |
| All subtypes work without runtime surprises | Any impl can replace the interface safely | ✅ LSP satisfied |

---

## Interview Answer Template

> "LSP means any subtype must be substitutable for its parent without the program breaking.
>
> The classic violation is when a subclass throws `UnsupportedOperationException` on an
> inherited method — it implements the interface but doesn't honor its contract.
>
> In my code, `ReadOnlyUserCache` implemented `UserRepository` but threw on `save()` and
> `delete()`. Any caller using `UserRepository` would crash at runtime if it got a cache.
>
> The fix was to split the interface — `ReadOnlyUserCache` now only implements
> `ReadableUserRepository`, which it can fully honor. The compiler now prevents it from
> being passed anywhere writes are expected. Bug moved from runtime crash → compile error."

---

## SOLID Quick Recap (where LSP fits)

```
S — Single Responsibility  →  One reason to change
O — Open/Closed            →  Open for extension, closed for modification
L — Liskov Substitution    →  Subtypes must honor parent's contract  ← YOU ARE HERE
I — Interface Segregation  →  Don't force implementors to implement unused methods
D — Dependency Inversion   →  Depend on abstractions, not concretions
```
