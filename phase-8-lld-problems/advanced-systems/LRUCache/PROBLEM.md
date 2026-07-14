# Problem: LRU Cache
> Domain: Systems | Difficulty: Medium | Est. Time: 45 min | Interview Frequency: 🔥 Very Common

---

## Problem Statement

Design an LRU (Least Recently Used) Cache with extensible eviction policies.

Requirements:
1. Cache has a fixed capacity
2. On `get(key)` — return value if exists, mark as recently used. Return -1 if not found.
3. On `put(key, value)` — insert or update. If at capacity, evict the least recently used entry before inserting.
4. Eviction policy must be pluggable — LRU today, LFU or FIFO tomorrow
5. Thread-safe under concurrent reads and writes

---

## Clarifying Questions to Ask

- Is this a generic cache or a specific type (int→int)?
- What's the expected read/write ratio? (Affects locking strategy)
- Is TTL (time-to-live) per key needed?
- Is the eviction policy the same for all keys or configurable per key?
- Single JVM or distributed?

---

## Key Patterns (Hints)

<details>
<summary>Click to reveal</summary>

- **Strategy** — eviction policy (LRU, LFU, FIFO) as pluggable strategy
- **Decorator** — add thread safety, TTL, or metrics on top of base cache

</details>

---

## Data Structure Hint

<details>
<summary>Click to reveal</summary>

HashMap + Doubly Linked List gives O(1) get and put for LRU.
- HashMap: key → node reference
- Doubly Linked List: maintains usage order (head = most recent, tail = least recent)

</details>

---

## Your Task

1. Implement `Cache<K, V>` interface with `get` and `put`
2. Implement `LRUCache` using the optimal data structure
3. Demonstrate strategy swap: same cache interface, LFUCache as alternate impl
4. Implement in `src/main/java/com/lld/phase8/problems/advanced/lrucache/`

---

## Edge Cases

- `get` on an empty cache
- `put` when capacity is 1 — evict immediately
- Same key put twice — update value, move to most recent
- Concurrent writes from 2 threads at capacity — race on eviction
