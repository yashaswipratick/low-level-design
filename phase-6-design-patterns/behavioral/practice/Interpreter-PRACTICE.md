# Interpreter — Practice Problems
> Goal: Evaluate sentences in a simple language by representing grammar rules as objects.

---

## Key Intuition
**Interpreter = translate and execute.** You have a simple language (math expressions, filters, rules). You want to parse it and execute it. Each grammar rule becomes a class.

`"3 + 4 * 2"` → tree: `Add(3, Multiply(4, 2))` → evaluate → `11`

**When to think Interpreter:** You're building a rule engine, query parser, calculator, or any DSL (Domain-Specific Language).

---

## Problem 1: Math Expression Evaluator

### Scenario
You receive math expressions as strings. You want to evaluate them.

Expressions supported:
- Numbers: `5`, `42`, `100`
- Addition: `3 + 4`
- Multiplication: `3 * 4`  
- Combinations: `3 + 4 * 2` (should respect precedence: `3 + (4*2) = 11`)

For simplicity in this problem, expressions are pre-parsed into a tree (you receive the object structure, not the string — parsing is a separate concern).

### Your Task
1. `Expression` interface: `int interpret()`
2. `NumberExpression` (Terminal) — wraps a constant
3. `AddExpression`, `MultiplyExpression`, `SubtractExpression` (Non-Terminal) — hold two child expressions

<details>
<summary>🔍 Hint</summary>

Terminal expressions are leaves (just a number, no children).
Non-terminal expressions have left and right children (which can themselves be terminals OR non-terminals).

```java
// (3 + 4) * 2 as an object tree:
Expression expr = new MultiplyExpression(
    new AddExpression(new NumberExpression(3), new NumberExpression(4)),
    new NumberExpression(2)
);
expr.interpret();  // → 14
```

</details>

<details>
<summary>✅ Pattern Reveal</summary>

**Pattern: Interpreter**

Why: You have a language (arithmetic) with grammar rules. Each rule is a class. Complex expressions are built by composing simpler ones — just like Composite, but the key purpose is EVALUATION (interpretation).

</details>

### Starter Code

```java
// Expression interface — this IS the Interpreter interface
public interface Expression {
    int interpret();
    String toFormula();  // human-readable
}

// Terminal Expression — a leaf (no children)
public class NumberExpression implements Expression {
    private final int value;

    public NumberExpression(int value) { this.value = value; }

    @Override
    public int interpret() { return value; }

    @Override
    public String toFormula() { return String.valueOf(value); }
}

// Non-Terminal Expression — has two children
public class AddExpression implements Expression {
    private final Expression left;
    private final Expression right;

    public AddExpression(Expression left, Expression right) {
        this.left = left; this.right = right;
    }

    @Override
    public int interpret() {
        return left.interpret() + right.interpret();  // recursive evaluation
    }

    @Override
    public String toFormula() {
        return "(" + left.toFormula() + " + " + right.toFormula() + ")";
    }
}

// TODO: MultiplyExpression, SubtractExpression, DivideExpression

// Test:
class Main {
    public static void main(String[] args) {
        // 3 + 4
        Expression simpleAdd = new AddExpression(new NumberExpression(3), new NumberExpression(4));
        System.out.println(simpleAdd.toFormula() + " = " + simpleAdd.interpret());  // (3 + 4) = 7

        // (3 + 4) * 2
        Expression expr = new MultiplyExpression(
            new AddExpression(new NumberExpression(3), new NumberExpression(4)),
            new NumberExpression(2)
        );
        System.out.println(expr.toFormula() + " = " + expr.interpret());  // ((3 + 4) * 2) = 14

        // 10 - (2 * 3)
        Expression expr2 = new SubtractExpression(
            new NumberExpression(10),
            new MultiplyExpression(new NumberExpression(2), new NumberExpression(3))
        );
        System.out.println(expr2.toFormula() + " = " + expr2.interpret());  // (10 - (2 * 3)) = 4
    }
}
```

---

## Problem 2: Boolean Expression Evaluator (Rule Engine)

### Scenario
You have a rule engine that evaluates boolean conditions:
- `age > 18` — a comparison
- `country == "US"` — equality check
- `age > 18 AND country == "US"` — AND of two conditions
- `isPremium == true OR totalSpent > 1000` — OR of two conditions

A `Context` is a Map of variable names → values.

### Your Task
1. `BooleanExpression` interface: `boolean interpret(Map<String, Object> context)`
2. `GreaterThanExpression`, `EqualExpression` — compare a variable to a constant
3. `AndExpression`, `OrExpression`, `NotExpression` — combine boolean expressions

### Starter Code

```java
import java.util.Map;

// Boolean expression interface
public interface BooleanExpression {
    boolean interpret(Map<String, Object> context);
    String toRule();
}

// Terminal: variable > constant
public class GreaterThanExpression implements BooleanExpression {
    private final String variable;
    private final double threshold;

    public GreaterThanExpression(String variable, double threshold) {
        this.variable = variable;
        this.threshold = threshold;
    }

    @Override
    public boolean interpret(Map<String, Object> context) {
        Object value = context.get(variable);
        if (value instanceof Number num) {
            return num.doubleValue() > threshold;
        }
        return false;
    }

    @Override
    public String toRule() { return variable + " > " + threshold; }
}

// Terminal: variable == constant
public class EqualExpression implements BooleanExpression {
    private final String variable;
    private final Object expected;

    public EqualExpression(String variable, Object expected) {
        this.variable = variable;
        this.expected = expected;
    }

    @Override
    public boolean interpret(Map<String, Object> context) {
        return expected.equals(context.get(variable));
    }

    @Override
    public String toRule() { return variable + " == " + expected; }
}

// Non-terminal: AND
public class AndExpression implements BooleanExpression {
    private final BooleanExpression left;
    private final BooleanExpression right;

    public AndExpression(BooleanExpression left, BooleanExpression right) {
        this.left = left; this.right = right;
    }

    @Override
    public boolean interpret(Map<String, Object> context) {
        return left.interpret(context) && right.interpret(context);
    }

    @Override
    public String toRule() { return "(" + left.toRule() + " AND " + right.toRule() + ")"; }
}

// TODO: OrExpression, NotExpression

// Test:
class Main {
    public static void main(String[] args) {
        // Rule: age > 18 AND country == "US"
        BooleanExpression adultUs = new AndExpression(
            new GreaterThanExpression("age", 18),
            new EqualExpression("country", "US")
        );

        Map<String, Object> alice = Map.of("age", 25, "country", "US");
        Map<String, Object> bob   = Map.of("age", 17, "country", "US");
        Map<String, Object> carol = Map.of("age", 30, "country", "UK");

        System.out.println("Rule: " + adultUs.toRule());
        System.out.println("Alice: " + adultUs.interpret(alice));  // true
        System.out.println("Bob:   " + adultUs.interpret(bob));    // false (underage)
        System.out.println("Carol: " + adultUs.interpret(carol));  // false (wrong country)

        // Rule: age > 18 AND (country == "US" OR country == "UK")
        BooleanExpression adultUsOrUk = new AndExpression(
            new GreaterThanExpression("age", 18),
            new OrExpression(
                new EqualExpression("country", "US"),
                new EqualExpression("country", "UK")
            )
        );

        System.out.println("\nRule: " + adultUsOrUk.toRule());
        System.out.println("Alice: " + adultUsOrUk.interpret(alice));  // true
        System.out.println("Carol: " + adultUsOrUk.interpret(carol));  // true (UK is ok)
    }
}
```

---

## Problem 3: Simple Query Language

### Scenario
You want to filter a list of products using a simple query:

```
price < 1000 AND inStock == true
category == "ELECTRONICS" OR category == "BOOKS"
NOT (price > 500)
```

Re-use the `BooleanExpression` from Problem 2 — apply it to filter `List<Product>`.

### Starter Code

```java
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record Product(String name, String category, double price, boolean inStock) {
    // Convert to a map for the interpreter context
    public Map<String, Object> toContext() {
        return Map.of(
            "name", name,
            "category", category,
            "price", price,
            "inStock", inStock
        );
    }
}

public class ProductFilter {
    public List<Product> filter(List<Product> products, BooleanExpression rule) {
        return products.stream()
            .filter(p -> rule.interpret(p.toContext()))
            .collect(Collectors.toList());
    }
}

// Test:
class Main {
    public static void main(String[] args) {
        List<Product> catalog = List.of(
            new Product("Laptop", "ELECTRONICS", 999.99, true),
            new Product("Phone", "ELECTRONICS", 499.99, false),
            new Product("Java Book", "BOOKS", 49.99, true),
            new Product("Novel", "BOOKS", 19.99, true),
            new Product("Tablet", "ELECTRONICS", 799.99, true)
        );

        ProductFilter filter = new ProductFilter();

        // Filter: price < 800 AND inStock == true
        BooleanExpression rule1 = new AndExpression(
            new LessThanExpression("price", 800),   // TODO: implement LessThanExpression
            new EqualExpression("inStock", true)
        );

        System.out.println("Price < 800 AND inStock:");
        filter.filter(catalog, rule1).forEach(p -> System.out.println("  " + p.name() + " $" + p.price()));

        // Filter: category == BOOKS OR price < 100
        BooleanExpression rule2 = new OrExpression(
            new EqualExpression("category", "BOOKS"),
            new LessThanExpression("price", 100)
        );
        System.out.println("BOOKS or < $100:");
        filter.filter(catalog, rule2).forEach(p -> System.out.println("  " + p.name()));
    }
}
```
