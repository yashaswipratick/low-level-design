# SRP — Problem 2: The Overloaded Order Controller

## Your Task

Look at `OrderController.java`. The controller is doing 4 distinct things.

1. List each responsibility currently in the controller.
2. Which belongs in: controller vs service vs domain object?
3. Refactor so the controller's **only job is HTTP in/out** — no business logic inside it.

## Hints
- Controllers should only: parse request → call service → return response
- Business rules (tax calculation, stock validation) belong in a service
- Notification side-effects (email) belong in a dedicated sender class

## Expected Output
Create new files in this directory:
- `OrderService.java` (owns business logic)
- `OrderEmailSender.java` (owns email)
- `PricingCalculator.java` (owns tax logic)
- `OrderController.java` (refactored — just HTTP routing)
