# DIP — Problem 1: The Hardwired Notification Service

## Your Task

Look at `OrderService.java`. It hardwires `SendGridEmailClient` and `TwilioSmsClient` with credentials in source.

1. List every maintenance problem this causes (switching providers, testing, security, etc.).
2. Refactor using DIP: define abstractions, implement them, inject via constructor.

## Hints
- Define `EmailSender` and `SmsSender` interfaces
- `SendGridEmailSender` and `TwilioSmsSender` are `@Component` implementations
- Credentials come from `@Value("${...}")` — never hardcoded
- `OrderService` constructor accepts only the interfaces — never the concrete classes
- In tests: `new OrderService(mockRepo, mockEmail, mockSms)` — no Spring, no HTTP

## Expected Output
Create new files in this directory:
- `EmailSender.java` (interface)
- `SmsSender.java` (interface)
- `SendGridEmailSender.java` (@Component)
- `TwilioSmsSender.java` (@Component)
- `OrderService.java` (refactored — constructor injection, interfaces only)
- `OrderServiceTest.java` (unit test — no Spring context needed)
