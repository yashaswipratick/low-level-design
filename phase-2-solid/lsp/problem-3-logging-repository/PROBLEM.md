# LSP — Problem 3: The Logging Repository That Doesn't Persist

## Your Task

Look at `OrderRepository.java` and `LoggingOrderRepository.java`.

`LoggingOrderRepository.save()` prints to console but doesn't actually save.
`findById()` always returns empty because nothing was ever persisted.
Callers expect `findById()` to return what was previously `save()`-d — that contract is broken.

1. Is `LoggingOrderRepository` a valid LSP substitute for `OrderRepository`? Justify your answer.
2. Design a correct solution using the **Decorator pattern** — logging wraps a real repo rather than replacing it.

## Hints
- Decorator: `LoggingOrderRepository` takes a real `OrderRepository` as constructor arg
- It logs before/after, then **delegates** the actual operation to the inner repo
- The decorated version honors the full contract — logs AND persists
- For dry-run behavior, handle it in the **service layer** (explicit `if dryRun return`) not by faking the repo

## Expected Output
Create new files in this directory:
- `LoggingOrderRepository.java` (decorator — wraps real repo, delegates, honors contract)
- `OrderService.java` (shows dry-run handled at service level, not repo level)
