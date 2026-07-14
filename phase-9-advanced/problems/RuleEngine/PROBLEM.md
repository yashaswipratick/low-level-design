# Problem: Rule Engine
> Domain: Platform / Business Logic | Difficulty: Staff | Est. Time: 75 min | Interview Frequency: 📌 Occasional at Staff Level

---

## Problem Statement

Design a Rule Engine that evaluates dynamic business rules at runtime.

Requirements:
1. Rules are defined as expressions and stored in a database (not hardcoded)
2. Rules are loaded at startup and can be hot-reloaded without restarting the service
3. A rule evaluates a `fact` (a context object) and returns true/false or a value
4. Rules can be composed: AND, OR, NOT of simpler rules
5. Prioritized rule evaluation: rules run in priority order; first matching rule wins (short-circuit)
6. Rule versioning: multiple versions of the same rule can coexist; clients pick version
7. Performance: thousands of rules must evaluate in <10ms

---

## Clarifying Questions to Ask

- Is the expression language simple (comparison, AND/OR) or complex (arithmetic, function calls)?
- Are rules tenant-specific (per-customer) or global?
- What is the fact schema — fixed (Java POJO) or dynamic (Map)?
- Should rule conflicts (two rules match same condition) be reported?
- Is rule testing/preview (dry-run without side effects) in scope?
- Who writes rules — engineers or business users (affects expression complexity)?

---

## Key Patterns (Hints)

<details>
<summary>Click to reveal</summary>

- **Interpreter** — parse and evaluate rule expressions (the classic Interpreter pattern use case)
- **Composite** — AND/OR rules composed from leaf conditions
- **Strategy** — different rule evaluation strategies (first-match, all-match, scored)
- **Decorator** — add caching, logging, metrics around rule evaluation

</details>

---

## Class Design Starting Point

```java
// Rule definition (stored in DB, deserialized at load)
public record Rule(
    String ruleId,
    String name,
    int priority,
    Condition condition,     // the expression tree
    RuleAction action        // what to do when matched
) {}

// Condition — Composite pattern
public interface Condition {
    boolean evaluate(Fact fact);
}

// Leaf conditions
public record EqualsCondition(String field, Object value) implements Condition {
    public boolean evaluate(Fact fact) {
        return value.equals(fact.get(field));
    }
}

public record GreaterThanCondition(String field, Comparable value) implements Condition {
    public boolean evaluate(Fact fact) {
        return ((Comparable) fact.get(field)).compareTo(value) > 0;
    }
}

// Composite conditions
public record AndCondition(List<Condition> conditions) implements Condition {
    public boolean evaluate(Fact fact) {
        return conditions.stream().allMatch(c -> c.evaluate(fact));
    }
}

public record OrCondition(List<Condition> conditions) implements Condition {
    public boolean evaluate(Fact fact) {
        return conditions.stream().anyMatch(c -> c.evaluate(fact));
    }
}

// Fact — the context being evaluated
public class Fact {
    private final Map<String, Object> attributes;
    public Object get(String key) { return attributes.get(key); }
}

// Rule Engine
public class RuleEngine {
    private volatile List<Rule> rules;  // volatile for hot-reload visibility

    public Optional<RuleAction> evaluate(Fact fact) {
        return rules.stream()
            .sorted(Comparator.comparingInt(Rule::priority))
            .filter(rule -> rule.condition().evaluate(fact))
            .map(Rule::action)
            .findFirst();  // first-match wins
    }
}
```

---

## Your Task

1. Full `Condition` hierarchy: Equals, GreaterThan, Contains, And, Or, Not
2. `RuleParser` — deserialize rules from JSON DSL into `Condition` tree
3. `RuleEngine` with priority-ordered first-match evaluation
4. `HotReloadableRuleEngine` — polls for rule changes, atomically replaces rule set
5. `CachingRuleEngine` — Decorator that caches fact → result for identical repeated facts
6. Implement in `src/main/java/com/lld/phase9/ruleengine/`

---

## Example Rule DSL (JSON)

```json
{
  "ruleId": "premium-free-shipping",
  "priority": 10,
  "condition": {
    "type": "AND",
    "conditions": [
      { "type": "EQUALS", "field": "customerTier", "value": "PREMIUM" },
      { "type": "GREATER_THAN", "field": "orderTotal", "value": 500 }
    ]
  },
  "action": { "type": "APPLY_DISCOUNT", "discountCode": "FREE_SHIP" }
}
```

---

## Edge Cases

- Circular rule dependencies — Rule A triggers Rule B which triggers Rule A
- Rule references a field not present in the Fact — null safety
- All rules evaluate to false — no match — what is the default action?
- Hot reload during a high-traffic second — partial rule set visible
- Rule with 100 nested AND/OR conditions — stack overflow risk (recursive evaluation)
- Malformed rule in DB — fail the single rule or fail to load all rules?

---

## Trade-off: Build vs Buy

| Approach | Pros | Cons |
|----------|------|------|
| Custom Rule Engine (this problem) | Full control, optimized for your model | Maintenance burden |
| Drools (JBoss) | Mature, full DSL, RETE algorithm | Heavy dependency, steep learning curve |
| Spring SpEL | Built-in, simple expressions | Limited composition, Spring-coupled |
| Groovy scripts | Full programming language | Security risk (arbitrary code execution) |

> Interview answer: "I'd build a simple Interpreter-based engine for structured conditions. For complex financial or insurance rules, I'd evaluate Drools. I'd never allow arbitrary script execution without a sandbox."
