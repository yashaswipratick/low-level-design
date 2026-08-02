# O — Open/Closed Principle

> "Open for extension, closed for modification."

## The Key Question
When a new requirement arrives, can you satisfy it by **adding new code** without touching existing code?
If you must modify an existing method to add a new case — OCP is violated.

## Problems

| # | Problem | Smells |
|---|---------|--------|
| [P1](problem-1-discount-engine/) | DiscountService | `if/else` per customer type — grows forever |
| [P2](problem-2-notification-dispatcher/) | NotificationDispatcher | `if/else` per channel — adding Slack breaks existing code |
| [P3](problem-3-data-exporter/) | DataExporter | `if/else` per export format — risky to modify in prod |

## How to Spot OCP Violations
- `if/else` or `switch` branching on a **type string or enum**
- Adding a new variant requires touching an existing method
- The method has a comment like `// Adding X? Modify here`
