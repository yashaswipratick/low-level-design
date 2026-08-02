# SRP — Problem 1: The God Report Service

## Your Task

Look at `ReportService.java`. It currently does **three distinct things** in one class.

1. How many reasons does `ReportService` have to change? Name each one.
2. Refactor into properly scoped classes so each class has **exactly one reason to change**.

## Hints
- Think: "If the email provider changes, what class should I touch?"
- Think: "If the HTML layout changes, what class should I touch?"
- Think: "If the DB schema changes, what class should I touch?"
- Each answer should be a **different** class.

## Expected Output
Create new files in this directory:
- `SalesDataRepository.java`
- `ReportFormatter.java`
- `ReportEmailSender.java`
- `ReportService.java` (slim orchestrator — no business logic, just wires the above)
