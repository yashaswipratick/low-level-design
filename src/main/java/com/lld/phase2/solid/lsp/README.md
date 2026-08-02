# L — Liskov Substitution Principle

> "If S is a subtype of T, objects of type T may be replaced with objects of type S without altering the correctness of the program."

## The Key Question
Can you swap every subclass in place of its parent and have the program behave **correctly** (not just "not crash")?

## Problems

| # | Problem | Smells |
|---|---------|--------|
| [P1](problem-1-readonly-cache/) | ReadOnlyUserCache | implements `UserRepository` but throws on `save()` / `delete()` |
| [P2](problem-2-premium-account/) | PremiumAccount | `withdraw()` silently returns wrong amount — weakens postcondition |
| [P3](problem-3-logging-repository/) | LoggingOrderRepository | `save()` logs but doesn't persist — callers get incorrect state |

## How to Spot LSP Violations
1. Override throws `UnsupportedOperationException`
2. Override is an empty no-op
3. Caller does `instanceof` check before calling a method
4. Subclass weakens a precondition (parent requires non-null → child accepts null)
5. Subclass strengthens a postcondition (parent guarantees list → child returns empty)
