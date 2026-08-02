# ISP — Problem 3: The All-In-One Authentication Interface

## Your Task

Look at `AuthService.java` and `JwtAuthFilter.java`.

`AuthService` has 9 methods. `JwtAuthFilter` uses only `validateToken()` but depends on the entire interface.

1. What are the practical problems of this fat interface in tests and in future refactoring?
2. Split `AuthService` into focused interfaces. `JwtAuthFilter` should depend on **1 interface with ~3 methods**.

## Hints
- `TokenService` — generate, validate, invalidate (stateless, used by filters)
- `CredentialService` — authenticate, resetPassword, changePassword (used by account flows)
- `TwoFactorService` — enable, disable, generateCode, verifyCode (used by security settings)
- One `AuthServiceImpl` can implement all three — split is at the interface level

## Expected Output
Create new files in this directory:
- `TokenService.java`
- `CredentialService.java`
- `TwoFactorService.java`
- `AuthServiceImpl.java` (implements all three)
- `JwtAuthFilter.java` (refactored — depends on `TokenService` only)
- `AccountController.java` (depends on `CredentialService` + `TwoFactorService`)
