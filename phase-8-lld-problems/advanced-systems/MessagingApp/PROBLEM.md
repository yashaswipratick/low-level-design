# Problem: Messaging App (WhatsApp-style)
> Domain: Communication Platform | Difficulty: Hard | Est. Time: 60 min | Interview Frequency: 📌 Occasional

---

## Problem Statement

Design the core of a real-time messaging application.

Requirements:
1. One-to-one and group chats
2. Messages have delivery statuses: Sent → Delivered → Read (the two-tick model)
3. Users have online/offline/last-seen status
4. Support message types: text, image, video, document, location
5. Group chats: add/remove members, admin roles, group info
6. Messages can be replied to, forwarded, and deleted ("delete for everyone" vs "delete for me")
7. End-to-end encryption indicator (design the hook, not the crypto)

---

## Clarifying Questions to Ask

- Is message delivery real-time (WebSocket/long poll) or polling-based?
- Is message history stored on-device, server, or both?
- What is the max group size?
- Are read receipts per-member in groups (each tick is one member's read) or group-level?
- Is "delete for everyone" permanent or soft-delete with a replacement tombstone?
- Is message search in scope?
- Are voice/video calls in scope?

---

## Key Patterns (Hints)

<details>
<summary>Click to reveal</summary>

- **Observer** — online/offline presence events, message delivery status updates
- **Strategy** — message delivery mechanism (WebSocket, push notification, SMS fallback)
- **State** — message delivery state machine (Sent → Delivered → Read)
- **Composite** — `Chat` is either a `DirectChat` (leaf) or `GroupChat` (composite with members)
- **Command** — each message operation (send, delete, forward) is a command (supports undo for "delete for me")

</details>

---

## Class Design Starting Point

```
Chat (interface)
  ├── String getChatId()
  └── void sendMessage(Message message)

DirectChat implements Chat
  ├── User user1
  └── User user2

GroupChat implements Chat
  ├── List<GroupMember> members
  ├── String groupName
  └── void addMember(User user, GroupMember addedBy)

Message
  ├── String messageId
  ├── User sender
  ├── MessageContent content    // text, media, location
  ├── MessageState state        // SENT, DELIVERED, READ
  ├── Instant timestamp
  └── Message replyTo (nullable)

MessageState (enum)
  SENDING, SENT, DELIVERED, READ, DELETED_FOR_ME, DELETED_FOR_ALL

PresenceService (Observer target)
  └── void updateStatus(User user, UserStatus status)
```

---

## Your Task

1. `DirectChat` and `GroupChat` with Composite design
2. `Message` state machine: valid transitions, who can trigger each
3. `PresenceService` as an Observable — notifies contacts when user comes online
4. `MessageDeliveryService` — updates status when recipient's device acknowledges
5. "Delete for everyone" vs "delete for me" — model the difference cleanly
6. Implement in `src/main/java/com/lld/phase8/problems/advanced/messaging/`

---

## Edge Cases

- Message sent when recipient is offline — how is "delivered" triggered later?
- Group message: show "Delivered" when ALL members get it or ANY one?
- User leaves group — can they see old messages?
- "Delete for everyone" after 5 minutes — time limit enforcement
- Two users simultaneously create the same group — idempotency
- Message with media: text part delivered but media upload fails — partial state?

---

## Extension Points

- Message reactions (emoji) → `Reaction` entity linked to `Message`
- Broadcast lists (one sender, many independent recipients) → separate from `GroupChat`
- Message expiry (disappearing messages) → `TTLDecorator` on `MessageStore`
- Read receipts in groups → per-member `MessageDeliveryStatus` tracking
