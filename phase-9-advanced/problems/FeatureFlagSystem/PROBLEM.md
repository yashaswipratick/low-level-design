# Problem: Feature Flag System
> Domain: Platform / Release Management | Difficulty: Staff | Est. Time: 60 min | Interview Frequency: 📌 Occasional at Staff Level

---

## Problem Statement

Design a Feature Flag (Feature Toggle) system for safe gradual rollouts.

Requirements:
1. Flags can be: Boolean (on/off), Percentage Rollout (0–100%), and User/Segment Targeting
2. Flag evaluation is in the hot path — every API call evaluates flags, must be <1ms
3. Flag changes propagate to all application instances within 30 seconds
4. A/B testing: flag assigns users deterministically to variants (same user always gets same variant)
5. Kill switch: a flag can be instantly disabled to roll back a feature
6. Audit log: who changed what flag, when, and to what value
7. SDK: application code calls `featureFlags.isEnabled("new-checkout", userId)`

---

## Clarifying Questions to Ask

- Is flag targeting based on user ID, user attributes (country, tier), or both?
- Is there a flag inheritance hierarchy (org → team → service → flag)?
- Are flag evaluations logged per-request (for analytics) or sampled?
- What happens if the flag service is unreachable — fail open or use cached state?
- Is multivariate testing (more than 2 variants) in scope?
- Are scheduled flag changes (auto-enable at 9am) in scope?

---

## Key Patterns (Hints)

<details>
<summary>Click to reveal</summary>

- **Strategy** — different targeting strategies (percentage, user list, attribute match)
- **Decorator** — add caching, circuit breaker, metrics around flag evaluation
- **Observer** — flag changes propagate to SDK clients via push or poll
- **Chain of Responsibility** — evaluation rules: user override → segment → percentage → default

</details>

---

## Class Design Starting Point

```java
// Flag definition
public record FeatureFlag(
    String key,
    boolean defaultValue,
    List<TargetingRule> rules,  // evaluated in order; first match wins
    PercentageRollout rollout
) {}

// Targeting Rule — Chain of Responsibility
public interface TargetingRule {
    Optional<Boolean> evaluate(EvaluationContext context);
}

// User-specific override
public record UserTargetingRule(Set<String> userIds, boolean value) implements TargetingRule {
    public Optional<Boolean> evaluate(EvaluationContext context) {
        return userIds.contains(context.userId())
            ? Optional.of(value)
            : Optional.empty();
    }
}

// Attribute-based targeting (country, tier, etc.)
public record AttributeTargetingRule(String attribute, Set<String> values, boolean value)
    implements TargetingRule {}

// Percentage rollout — deterministic by userId hash
public record PercentageRollout(int percentage) {
    public boolean includes(String userId) {
        int hash = Math.abs(userId.hashCode() % 100);
        return hash < percentage;
    }
}

// Evaluation context
public record EvaluationContext(
    String userId,
    Map<String, String> attributes  // country, tier, platform, etc.
) {}

// SDK interface
public interface FeatureFlagClient {
    boolean isEnabled(String flagKey, EvaluationContext context);
    <T> T getVariant(String flagKey, EvaluationContext context, Class<T> type);
}
```

---

## Your Task

1. `FeatureFlagEvaluator` — evaluates a flag for a given `EvaluationContext` using chain of rules
2. `PercentageRollout` with deterministic hash (same user always gets same bucket)
3. `LocalCacheClient` — caches flag definitions in memory, refreshes every 30s
4. `FlagChangeNotifier` — push-based update (simulate with periodic poll for now)
5. `CircuitBreakerFlagClient` — decorator; on backend failure, use cached state
6. `AuditLogger` — records all flag mutations (key, old value, new value, changedBy, timestamp)
7. Implement in `src/main/java/com/lld/phase9/featureflags/`

---

## Percentage Rollout — Consistency Explained

```java
// BAD: random — same user gets different variant on every call
boolean enabled = Math.random() < 0.1;  // 10% rollout but inconsistent!

// GOOD: hash-based — same userId always maps to same bucket
public boolean isInRollout(String userId, int rolloutPercentage) {
    // MurmurHash or simpler: consistent hash function
    int bucket = Math.abs(userId.hashCode()) % 100;
    return bucket < rolloutPercentage;
}
// User "user-123" → hash → bucket 42 → always in 50% rollout → always sees flag
```

---

## Edge Cases

- Flag key does not exist — return default value or throw?
- Context has missing attributes referenced by targeting rule — skip rule or treat as non-match?
- Percentage rollout = 0 and a user is in the explicit user override list — override wins
- Flag updated from 50% to 0% — in-flight requests with cached value → lag window
- Very high cardinality userId space — hash collision probability in percentage rollout
- A/B test: user's attributes change (gets promoted to PREMIUM tier mid-experiment) — which variant?

---

## Real-World Connection

This maps to tools like:
- **LaunchDarkly** — industry standard feature flag service
- **Unleash** — open-source alternative  
- **Walmart's internal feature flag service** — similar targeting rules

> Interview answer: "I'd use LaunchDarkly in production rather than build. But knowing how to design it shows you understand: consistent hashing, distributed caching, circuit breaker patterns, and the CAP theorem trade-offs of flag propagation."
