# OCP — Problem 1: The Expanding Discount Engine

## Your Task

Look at `DiscountService.java`. Every time a new customer type is added, this method must be modified.
The PM has already announced: next sprint adds `SENIOR_CITIZEN` (15%) and `VETERAN` (25%).

1. What must change in this class every time a new type is added? Why is this risky?
2. Refactor using the **Strategy pattern** so new discount types can be added without touching `DiscountService`.

## Hints
- Define a `DiscountStrategy` interface with one method: `apply(Order order)`
- Each customer type = one class implementing `DiscountStrategy`
- Use Spring `@Component("TYPE_NAME")` so they auto-register
- `DiscountService` holds a `Map<String, DiscountStrategy>` — never changes again

## Expected Output
Create new files in this directory:
- `DiscountStrategy.java` (interface)
- `PremiumDiscount.java`
- `StudentDiscount.java`
- `EmployeeDiscount.java`
- `SeniorCitizenDiscount.java` ← new type, zero changes to service
- `DiscountService.java` (refactored — map dispatch, no if/else)
