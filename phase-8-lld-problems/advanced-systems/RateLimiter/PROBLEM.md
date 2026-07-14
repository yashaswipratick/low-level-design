# Problem: Rate Limiter
> Domain: Platform/API Gateway | Difficulty: Hard | Est. Time: 60 min | Interview Frequency: 🔥 Very Common

---

## Problem Statement

Design a Rate Limiter that restricts how many requests a client can make in a time window.

Requirements:
1. Support multiple rate limiting algorithms: Token Bucket, Leaky Bucket, Sliding Window Log, Fixed Window Counter
2. Limits can be applied at different levels: per-user, per-IP, per-API-endpoint, per-client-app
3. When limit is exceeded, return 429 Too Many Requests with retry-after header
4. Algorithm must be swappable without changing the rate limiter interface
5. Rules can stack — a user might be limited at 100 req/min globally AND 10 req/min on a specific endpoint

---

## Clarifying Questions to Ask

- Is the rate limiter in-process (same JVM) or as a shared service (Redis-backed)?
- Hard reject or queue excess requests?
- Is the algorithm the same for all endpoints or configurable per endpoint?
- How are distributed nodes handled — is shared state needed?
- What's the precision required — approximate OK or exact?

---

## Key Patterns (Hints)

<details>
<summary>Click to reveal</summary>

- **Strategy** — each algorithm (Token Bucket, Leaky Bucket, etc.) is a strategy
- **Decorator** — stack multiple limiters (per-user on top of per-endpoint)
- **Chain of Responsibility** — check user limit → endpoint limit → global limit in sequence

</details>

---

## Your Task

1. Design `RateLimiter` interface with `boolean allowRequest(String clientId)` 
2. Implement TokenBucketRateLimiter and FixedWindowRateLimiter
3. Show stacking: user must pass both user-level and endpoint-level limiter
4. Implement in `src/main/java/com/lld/phase8/problems/advanced/ratelimiter/`

---

## Edge Cases

- Clock skew in distributed environment — how does window reset?
- Client sends burst of 100 requests at the same millisecond
- Rate limit config changes at runtime — hot reload
- Different rate limits for free vs premium users
