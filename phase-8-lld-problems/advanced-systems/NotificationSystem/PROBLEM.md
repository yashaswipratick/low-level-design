# Problem: Notification System
> Domain: Platform | Difficulty: Hard | Est. Time: 60 min | Interview Frequency: ⭐ Common

---

## Problem Statement

Design a Notification System that sends alerts to users across multiple channels.

Requirements:
1. Support channels: Email, SMS, Push Notification, In-App, Slack
2. Users configure their channel preferences (e.g., only email for marketing, push + SMS for order updates)
3. Notifications have types (Order Update, Promo, Alert, Reminder) with different priority levels
4. Deduplication: same notification to same user within a time window should not be sent twice
5. Retry: failed channel deliveries retry with exponential backoff (up to 3 attempts)
6. Audit trail: every send attempt is logged with status
7. New channels can be added without touching existing code

---

## Clarifying Questions to Ask

- Is sending synchronous (wait for delivery confirmation) or fire-and-forget?
- Can a user be notified on multiple channels for the same event (fan-out)?
- Is user preference per notification type or global?
- What is the deduplication window per notification type?
- Is there a priority queue — critical alerts bypass the queue?
- Are templates per notification type or free-form messages?

---

## Key Patterns (Hints)

<details>
<summary>Click to reveal</summary>

- **Observer** — domain events (OrderPlaced, PriceDrop) trigger the notification pipeline
- **Strategy** — each channel (Email, SMS, Push) is a pluggable strategy
- **Chain of Responsibility** — preference check → dedup check → rate limit → send
- **Decorator** — wrap any channel with `RetryDecorator`, `AuditDecorator`, `RateLimitDecorator`
- **Factory** — create the appropriate notifier chain based on user preferences and notification type

</details>

---

## Class Design Starting Point

```
NotificationService
  └── notify(NotificationRequest request)

NotificationRequest
  ├── String userId
  ├── NotificationType type
  ├── Priority priority
  └── Map<String, Object> payload

NotificationChannel (interface)
  └── void send(Notification notification)

UserPreferenceService
  └── List<NotificationChannel> getChannelsFor(String userId, NotificationType type)

DeduplicationStore
  └── boolean isDuplicate(String userId, NotificationType type, Duration window)

NotificationAuditLog
  └── void record(String userId, String channel, Status status, Instant timestamp)
```

---

## Your Task

1. `NotificationService` fans out to all preferred channels for a user
2. Implement `EmailChannel`, `SmsChannel`, `PushChannel`
3. Wrap each channel with `RetryDecorator` (3 retries, exponential backoff)
4. Add `DeduplicatingDecorator` that suppresses repeat notifications in a configurable window
5. `OrderEventListener` (Observer) triggers notification on order state change
6. Implement in `src/main/java/com/lld/phase8/problems/advanced/notifications/`

---

## Edge Cases

- User has no channel preferences configured → use system default
- All channels fail after all retries → dead letter queue
- User opts out of all notifications → still send critical security alerts?
- Push token expired → fall back to email automatically
- Notification storm: 10,000 users notified of flash sale simultaneously

---

## Extension Points

- New channel (WhatsApp) → implement `NotificationChannel`
- New filter (quiet hours — no notifications 10pm–8am) → `QuietHoursDecorator`
- Template engine → inject message templates per type and channel
- Priority queue → `PriorityNotificationService` sorts by `Priority` before dispatch
