# LSP — Problem 1: The Read-Only Cache Breaking the Contract

## Your Task

Look at `UserRepository.java` and `ReadOnlyUserCache.java`.

The cache implements `UserRepository` but throws `UnsupportedOperationException` on `save()` and `delete()`.

1. Explain why `ReadOnlyUserCache` violates LSP even though it correctly "implements" the interface.
2. Refactor the interface hierarchy so a read-only cache is a **valid, safe substitution** — no throws anywhere.

## Hints
- Split `UserRepository` into `ReadableUserRepository` and `WritableUserRepository`
- `ReadOnlyUserCache` implements only `ReadableUserRepository`
- Full JPA repo implements a composed `UserRepository extends Readable + Writable`
- Caller methods should declare the **narrowest type** they actually need

## LSP Test
After your fix: Can you substitute `ReadOnlyUserCache` anywhere a `ReadableUserRepository` is expected without behavior breaking? If yes — LSP satisfied.

## Expected Output
Create new files in this directory:
- `ReadableUserRepository.java`
- `WritableUserRepository.java`
- `UserRepository.java` (extends both — for full implementations)
- `ReadOnlyUserCache.java` (implements only `ReadableUserRepository`)
- `JpaUserRepository.java` (implements `UserRepository`)
