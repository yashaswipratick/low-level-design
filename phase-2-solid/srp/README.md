# S — Single Responsibility Principle

> "A class should have only ONE reason to change."

## The Key Question
Don't ask "does this class do one thing?" — ask **"what would make me change this class?"**
If you can give two different answers, SRP is violated.

## Problems

| # | Problem | Smells |
|---|---------|--------|
| [P1](problem-1-report-service/) | ReportService | fetch + format + email in one class |
| [P2](problem-2-order-controller/) | OrderController | business logic leaking into HTTP layer |
| [P3](problem-3-user-class/) | User domain class | domain object managing its own persistence & serialization |

## How to Spot SRP Violations
- Class name ends in `Manager`, `Helper`, `Utils`, `Handler`
- Method list spans multiple unrelated concerns
- You need to change the class for more than one type of business requirement
