# DIP — Problem 2: The Field-Injected Mess

## Your Task

Look at `InventoryService.java`. All dependencies are `@Autowired` on private fields.

1. Give **3 concrete reasons** why field injection is considered bad practice in production code.
2. Refactor to constructor injection. Explain why making fields `final` matters.

## Hints
- Constructor injection: Spring auto-detects since Spring 4.3 (no `@Autowired` annotation needed)
- `final` fields = object fully initialized after construction, immutable, thread-safe
- After refactor: `new InventoryService(mockRepo, mockWh, mockAudit)` works without Spring
- Test becomes a pure unit test — no `@SpringBootTest`, no `@MockBean`, no context startup

## Expected Output
Modify in this directory:
- `InventoryService.java` (refactored to constructor injection with `final` fields)
- `InventoryServiceTest.java` (pure unit test — instantiate with `new`, no Spring)
