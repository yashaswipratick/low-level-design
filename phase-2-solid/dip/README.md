# D — Dependency Inversion Principle

> "High-level modules should not depend on low-level modules. Both should depend on abstractions."

## The Key Question
Does your business logic class (`OrderService`, `ProductService`) reference a **concrete** class directly?
If yes — it's coupled to that implementation. DIP is violated.

## Problems

| # | Problem | Smells |
|---|---------|--------|
| [P1](problem-1-notification-service/) | OrderService | `new SendGridEmailClient(...)` hardwired, API key in source |
| [P2](problem-2-field-injection/) | InventoryService | `@Autowired` field injection — hidden deps, untestable |
| [P3](problem-3-analytics/) | ProductService | Mixpanel SDK `new`-ed directly in business logic |

## How to Spot DIP Violations
- `new ConcreteClass()` inside a service or controller
- `@Autowired` on private fields (not constructor)
- Unit test requires Spring context to instantiate a class
- "To switch providers I have to change the service" — coupled to impl
