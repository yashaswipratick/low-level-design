# I — Interface Segregation Principle

> "Clients should not be forced to depend on interfaces they don't use."

## The Key Question
Does every class that implements this interface actually **use** every method?
If any implementor throws or leaves methods empty — ISP is violated.

## Problems

| # | Problem | Smells |
|---|---------|--------|
| [P1](problem-1-worker/) | Worker interface | `RobotWorker` forced to implement `eat()`, `sleep()`, `submitTimesheet()` |
| [P2](problem-2-product-repository/) | ProductRepository | 10-method interface, catalog service uses 3 |
| [P3](problem-3-auth-service/) | AuthService | 9-method fat interface, gateway filter uses only `validateToken()` |

## How to Spot ISP Violations
- Interface has 8+ methods spanning different concerns
- Implementors throw `UnsupportedOperationException` on some methods
- Callers mock 9 methods just to test 1
- Class names: `XxxManager`, `XxxFacade` with methods from multiple domains
