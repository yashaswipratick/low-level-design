# SRP — Problem 3: The Multi-Tool User Class

## Your Task

Look at `User.java`. A domain object is saving itself to DB, serializing itself to JSON, and validating its own email.

1. Why is coupling persistence + serialization + validation inside a domain object a problem?
2. Refactor so `User` only models **state and core domain behavior**.

## Hints
- Domain objects should be pure — no DB calls, no JSON, no framework annotations
- `checkPassword()` is fine in `User` — it's a core domain behavior
- Persistence → belongs in a Repository
- Serialization → belongs in a DTO Mapper
- Validation → belongs in a Validator component

## Expected Output
Create new files in this directory:
- `User.java` (pure domain — state + domain behavior only)
- `UserRepository.java` (persistence)
- `UserDtoMapper.java` (serialization to response DTO)
- `UserValidator.java` (email/input validation)
