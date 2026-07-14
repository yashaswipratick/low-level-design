# Problem: Distributed Rate Limiter
> Domain: Platform / Infrastructure | Difficulty: Staff | Est. Time: 90 min | Interview Frequency: ⭐ Common at Staff Level

---

## Problem Statement

Design a Rate Limiter that works across multiple application instances sharing a Redis backend.

Requirements:
1. All known in-process rate limiter algorithms (Token Bucket, Sliding Window Log)
2. Limits must be enforced consistently across 10 horizontally scaled application nodes
3. A limit of 100 req/min must not allow 200 requests when two nodes each think there are 50
4. Minimum latency overhead — every API call goes through this
5. Graceful degradation: if Redis is unavailable, fall back to in-process limiting (not hard fail)
6. Metrics: track how many requests were allowed, throttled, and fell back per client

---

## Clarifying Questions to Ask

- Is exact precision required, or is approximate enforcement acceptable?
- What is the acceptable latency budget for the rate limit check?
- Is Redis always available, or should we assume failures?
- Should the fallback (Redis down) be permissive (allow all) or restrictive (use last known limit)?
- Are rate limit configs stored in Redis or in the application config?
- Is per-request or per-second granularity needed?

---

## Key Concepts Required

- **Redis INCR + EXPIRE** — atomic counter increment with TTL for Fixed Window
- **Redis Sorted Set** — timestamps as scores for Sliding Window Log
- **Lua scripts** — atomic multi-step Redis operations (check-and-increment without race)
- **CAS (Compare-And-Set)** — optimistic concurrency on shared counters
- **Circuit Breaker** — detect Redis failures and switch to fallback mode

---

## Design Hint

<details>
<summary>Click to reveal</summary>

**Sliding Window with Redis Sorted Set:**

```
Key: ratelimit:{clientId}:{endpoint}
Type: Sorted Set (score = timestamp in ms, member = unique request ID)

Algorithm:
  1. Remove all members with score < (now - window_size)  // purge old entries
  2. Count remaining members
  3. If count < limit → ZADD current request with score=now → ALLOW
  4. Else → REJECT

All 3 steps in one Lua script for atomicity.
```

**Token Bucket with Redis Hash:**

```
Key: ratelimit:tokenbucket:{clientId}
Fields: tokens (current tokens), last_refill (timestamp)

On each request (Lua script):
  1. Calculate elapsed = now - last_refill
  2. Add tokens = elapsed * refill_rate (cap at max_tokens)
  3. If tokens >= 1 → decrement tokens, update last_refill → ALLOW
  4. Else → REJECT
```

</details>

---

## Your Task

1. `DistributedRateLimiter` interface with `boolean allowRequest(String clientId, String endpoint)`
2. `SlidingWindowRedisLimiter` using sorted sets + Lua script
3. `TokenBucketRedisLimiter` using hash + Lua script
4. `CircuitBreakerRateLimiter` — decorates any limiter, falls back to in-process on Redis failure
5. `CompositeRateLimiter` — chains user-level + endpoint-level limiters (all must pass)
6. Implement in `src/main/java/com/lld/phase9/ratelimiter/`

---

## Edge Cases

- Two nodes execute Lua script at exact same millisecond — is it still safe?
- Redis returns TIMEOUT (not down, just slow) — is this treated as failure?
- Clock skew between app nodes — one node's "now" is 2 seconds ahead
- Rate limit config changes: 100 req/min → 200 req/min — do existing windows reset?
- Client ID spoofing — rate limit per authenticated user vs per IP
- Memory growth: sorted set for sliding window keeps growing if client never requests again

---

## Trade-off Discussion Points

| Approach | Consistency | Latency | Complexity |
|----------|-------------|---------|------------|
| In-process only | Node-local (10x over-limit possible) | <0.01ms | Low |
| Redis Fixed Window | Approximate (boundary burst) | ~1ms | Low |
| Redis Sliding Window Log | Exact | ~1-2ms | Medium |
| Redis Token Bucket (Lua) | Exact | ~1ms | Medium |
| Distributed token bucket with replication lag | Approximate | ~2ms | High |

> Staff question: "When would you accept approximate rate limiting over exact?"
> Answer: For user-facing APIs (slight over-limit acceptable), exact for billing/abuse prevention.
