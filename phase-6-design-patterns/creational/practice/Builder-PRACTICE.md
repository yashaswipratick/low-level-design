# Builder — Practice Problems
> Goal: Recognize when object construction has many optional parts. Stop using 10-parameter constructors.

---

## Problem 1: Pizza Order

### Scenario
You're building an ordering system for a pizza shop. A pizza has:
- **Required:** size (SMALL/MEDIUM/LARGE), crust type (THIN/THICK/STUFFED)
- **Optional:** sauce (tomato/white/BBQ — default: tomato), cheese (Mozzarella — default: yes), toppings (pepperoni, mushroom, olives, etc. — any combination)

A customer came in and ordered: "Large pizza, thin crust, BBQ sauce, no cheese, pepperoni and mushrooms."

Another ordered: "Small pizza, stuffed crust." (all defaults)

A developer wrote this:

```java
Pizza p1 = new Pizza("LARGE", "THIN", "BBQ", false, List.of("pepperoni", "mushrooms"));
Pizza p2 = new Pizza("SMALL", "STUFFED", "tomato", true, List.of());
```

**What problems do you see? How do you fix it?**

### Problems with the Current Approach
- What does `false` mean? Is it "no cheese" or "no delivery"?
- What if someone passes `null` for toppings?
- Hard to add new optional fields later without breaking all callers

### Your Task
1. Which pattern solves this?
2. Rewrite `Pizza` with a `Builder` so it reads naturally
3. The result should be immutable (`final` fields, no setters)

<details>
<summary>🔍 Hint</summary>
The Builder pattern uses a separate `Pizza.Builder` class with chainable setter methods. The final `build()` call creates and returns the immutable `Pizza`.
</details>

<details>
<summary>✅ Pattern Reveal</summary>

**Pattern: Builder**

Why: An object with many optional parameters that should be readable, immutable, and validated at construction time. Instead of `new Pizza("LARGE", "THIN", false, null, ...)`, you write `new Pizza.Builder("LARGE", "THIN").bbqSauce().noSheese().topping("pepperoni").build()`.

</details>

### Starter Code

```java
import java.util.ArrayList;
import java.util.List;

public final class Pizza {
    // Required fields — always set
    private final String size;
    private final String crust;

    // Optional fields — have defaults
    private final String sauce;
    private final boolean hasCheese;
    private final List<String> toppings;

    // Private constructor — only Builder can create Pizza
    private Pizza(Builder builder) {
        this.size      = builder.size;
        this.crust     = builder.crust;
        this.sauce     = builder.sauce;
        this.hasCheese = builder.hasCheese;
        this.toppings  = List.copyOf(builder.toppings);
    }

    @Override
    public String toString() {
        return size + " pizza | Crust: " + crust + " | Sauce: " + sauce
               + " | Cheese: " + hasCheese + " | Toppings: " + toppings;
    }

    public static class Builder {
        // Required
        private final String size;
        private final String crust;

        // Optional — with defaults
        private String sauce = "tomato";
        private boolean hasCheese = true;
        private List<String> toppings = new ArrayList<>();

        // Required params go in the Builder constructor
        public Builder(String size, String crust) {
            if (size == null || crust == null) throw new IllegalArgumentException("size and crust are required");
            this.size = size;
            this.crust = crust;
        }

        // TODO: add fluent methods for optional fields
        // public Builder sauce(String sauce) { ... return this; }
        // public Builder noCheese() { ... return this; }
        // public Builder topping(String topping) { ... return this; }

        public Pizza build() {
            return new Pizza(this);
        }
    }
}

// Test — should read like natural language:
class Main {
    public static void main(String[] args) {
        Pizza p1 = new Pizza.Builder("LARGE", "THIN")
            .sauce("BBQ")
            .noCheese()
            .topping("pepperoni")
            .topping("mushrooms")
            .build();

        Pizza p2 = new Pizza.Builder("SMALL", "STUFFED").build();  // all defaults

        System.out.println(p1);
        System.out.println(p2);
    }
}
```

### Expected Output
```
LARGE pizza | Crust: THIN | Sauce: BBQ | Cheese: false | Toppings: [pepperoni, mushrooms]
SMALL pizza | Crust: STUFFED | Sauce: tomato | Cheese: true | Toppings: []
```

---

## Problem 2: HTTP Request Builder

### Scenario
You need to build an `HttpRequest` object before sending it. An HTTP request has:
- **Required:** method (GET/POST/PUT), URL
- **Optional:** headers (map), body (string), timeout (default 30s), followRedirects (default true)

Without a builder, a developer writes:
```java
new HttpRequest("GET", "https://api.example.com/users", null, null, 30, true)
```

With a builder, it should look like:
```java
HttpRequest.builder("GET", "https://api.example.com/users")
    .header("Authorization", "Bearer token123")
    .header("Accept", "application/json")
    .timeout(10)
    .build();
```

### Your Task
Implement `HttpRequest` with a `Builder` that:
1. Requires method and URL in the builder constructor
2. Allows adding multiple headers via `.header(key, value)`
3. Validates: URL must start with "http", timeout must be > 0

<details>
<summary>✅ Pattern Reveal</summary>

**Pattern: Builder**

Why: HTTP requests have a variable number of headers (can't predict in constructor), optional body, and validation rules. Builder makes it readable and safe.

</details>

### Starter Code

```java
import java.util.HashMap;
import java.util.Map;

public final class HttpRequest {
    private final String method;
    private final String url;
    private final Map<String, String> headers;
    private final String body;
    private final int timeoutSeconds;
    private final boolean followRedirects;

    private HttpRequest(Builder b) {
        this.method = b.method;
        this.url = b.url;
        this.headers = Map.copyOf(b.headers);
        this.body = b.body;
        this.timeoutSeconds = b.timeoutSeconds;
        this.followRedirects = b.followRedirects;
    }

    @Override
    public String toString() {
        return method + " " + url + "\nHeaders: " + headers
               + "\nBody: " + body + "\nTimeout: " + timeoutSeconds + "s";
    }

    public static class Builder {
        private final String method;
        private final String url;
        private Map<String, String> headers = new HashMap<>();
        private String body = null;
        private int timeoutSeconds = 30;
        private boolean followRedirects = true;

        public Builder(String method, String url) {
            // TODO: validate url starts with "http"
            this.method = method;
            this.url = url;
        }

        // TODO: implement header(), body(), timeout(), followRedirects() methods

        public HttpRequest build() {
            // TODO: add any final validation
            return new HttpRequest(this);
        }
    }
}
```

---

## Problem 3: Email Message

### Scenario
An email has: `to` (required), `subject` (required), optionally `cc`, `bcc`, `replyTo`, `body`, and `attachments`.

Before sending, you must validate: `to` and `subject` are not blank, and `to` is a valid email format.

**Build an immutable `EmailMessage` using the Builder pattern with validation in `build()`.**

### Starter Code

```java
import java.util.List;
import java.util.ArrayList;

public final class EmailMessage {
    private final String to;
    private final String subject;
    private final String body;
    private final List<String> cc;
    private final List<String> bcc;
    private final List<String> attachments;

    private EmailMessage(Builder b) {
        this.to = b.to;
        this.subject = b.subject;
        this.body = b.body;
        this.cc = List.copyOf(b.cc);
        this.bcc = List.copyOf(b.bcc);
        this.attachments = List.copyOf(b.attachments);
    }

    public static class Builder {
        private final String to;
        private final String subject;
        private String body = "";
        private List<String> cc = new ArrayList<>();
        private List<String> bcc = new ArrayList<>();
        private List<String> attachments = new ArrayList<>();

        public Builder(String to, String subject) {
            this.to = to;
            this.subject = subject;
        }

        // TODO: body(), cc(), bcc(), attach() methods

        public EmailMessage build() {
            // Validate
            if (to == null || to.isBlank()) throw new IllegalStateException("'to' is required");
            if (subject == null || subject.isBlank()) throw new IllegalStateException("'subject' is required");
            if (!to.contains("@")) throw new IllegalStateException("'to' must be a valid email");
            return new EmailMessage(this);
        }
    }

    @Override
    public String toString() {
        return "To: " + to + "\nSubject: " + subject + "\nCC: " + cc + "\nBody: " + body;
    }
}

// Test:
// EmailMessage email = new EmailMessage.Builder("alice@example.com", "Hello!")
//     .body("Hi Alice, how are you?")
//     .cc("bob@example.com")
//     .attach("report.pdf")
//     .build();
// System.out.println(email);
```

---

## Problem 4: SQL Query Builder

### Scenario
You're building a query builder for a reporting tool. Users construct SQL-like queries:

```
SELECT name, age FROM users WHERE age > 18 ORDER BY name LIMIT 10
```

Without a builder, the code looks like:
```java
new Query("users", List.of("name","age"), "age > 18", "name", 10)
```

With a builder:
```java
Query.from("users")
     .select("name", "age")
     .where("age > 18")
     .orderBy("name")
     .limit(10)
     .build();
```

### Your Task
Implement `Query` with a `Builder`. If no `.select()` is called, default to `SELECT *`.

### Starter Code

```java
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public final class Query {
    private final String table;
    private final List<String> columns;
    private final String whereClause;
    private final String orderBy;
    private final int limit;

    private Query(Builder b) {
        this.table = b.table;
        this.columns = b.columns.isEmpty() ? List.of("*") : List.copyOf(b.columns);
        this.whereClause = b.whereClause;
        this.orderBy = b.orderBy;
        this.limit = b.limit;
    }

    @Override
    public String toString() {
        StringBuilder sql = new StringBuilder("SELECT ");
        sql.append(String.join(", ", columns));
        sql.append(" FROM ").append(table);
        if (whereClause != null) sql.append(" WHERE ").append(whereClause);
        if (orderBy != null) sql.append(" ORDER BY ").append(orderBy);
        if (limit > 0) sql.append(" LIMIT ").append(limit);
        return sql.toString();
    }

    public static Builder from(String table) {
        return new Builder(table);
    }

    public static class Builder {
        private final String table;
        private List<String> columns = new ArrayList<>();
        private String whereClause = null;
        private String orderBy = null;
        private int limit = -1;

        private Builder(String table) { this.table = table; }

        // TODO: implement select(), where(), orderBy(), limit() methods

        public Query build() {
            return new Query(this);
        }
    }
}

// Test:
// String sql = Query.from("users").select("name","age").where("age > 18").orderBy("name").limit(10).build().toString();
// Expected: SELECT name, age FROM users WHERE age > 18 ORDER BY name LIMIT 10
```
