# ISP — Problem 1: The Fat Worker Interface

## Your Task

Look at `Worker.java`, `HumanEmployee.java`, and `RobotWorker.java`.

`RobotWorker` is forced to implement `eat()`, `sleep()`, and `submitTimesheet()` — which it cannot do.

1. Group the 5 methods by concern (biological needs / professional duties / admin).
2. Split `Worker` into focused interfaces. Show how each implementor only implements what it can fulfill.

## Hints
- Break by capability, not by implementor
- `Workable`, `Feedable`, `Restable`, `MeetingCapable`, `Trackable` — or your own naming
- Caller methods should declare the **narrowest interface** they actually need
- After the fix: `runPayroll(Trackable worker)` won't accept a `RobotWorker` at compile time

## Expected Output
Create new files in this directory:
- One interface file per capability (e.g., `Workable.java`, `Feedable.java`, etc.)
- `HumanEmployee.java` (implements all applicable interfaces)
- `RobotWorker.java` (implements only what it can — no throws)
- `WorkScheduler.java` (caller — shows using narrow interface types)
