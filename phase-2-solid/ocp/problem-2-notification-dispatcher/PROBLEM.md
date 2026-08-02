# OCP — Problem 2: The Rigid Notification Dispatcher

## Your Task

Look at `NotificationDispatcher.java`. Adding a new channel (SLACK, WHATSAPP) requires modifying an existing method.

1. Why does adding a new channel break OCP in this design?
2. Refactor using Strategy + constructor-time registration so `NotificationDispatcher.dispatch()` **never needs to change**.

## Hints
- Define `NotificationSender` interface with `channel()` and `send(Notification)` methods
- Each channel = one `@Component` implementing `NotificationSender`
- Dispatcher collects all senders via `List<NotificationSender>` constructor injection
- Build a `Map<String, NotificationSender>` at construction time

## Expected Output
Create new files in this directory:
- `NotificationSender.java` (interface)
- `EmailNotificationSender.java`
- `SmsNotificationSender.java`
- `SlackNotificationSender.java` ← new channel added with zero changes to dispatcher
- `NotificationDispatcher.java` (refactored — map dispatch only)
