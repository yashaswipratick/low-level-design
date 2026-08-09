# Singleton — Practice Problems
> Goal: Recognize when ONE shared instance is the right answer. Then implement it correctly.

---

## How to Use These Problems

1. **Read the scenario** — do NOT look at hints or reveal
2. **Ask yourself:** What pattern fits here? Why?
3. **Write your answer** before opening any hint
4. **Implement** the solution
5. **Then** check the reveal and compare

---

## Problem 1: The App Config

### Scenario
You're building a Spring Boot app. Multiple classes need access to configuration values like `app.max-retry-count`, `app.timeout-seconds`, `app.environment`.

The configuration is loaded from a file at startup. Loading it takes 200ms (reads disk, parses YAML).

You notice `UserService`, `OrderService`, and `PaymentService` all need these config values. Every time someone calls `new AppConfig()`, it reads the file again — 200ms wasted each time.

**How do you ensure the config file is read EXACTLY ONCE, and every class gets the SAME instance?**

### Your Task
1. Which pattern solves this? Why?
2. Implement `AppConfig` so it can only be instantiated once
3. Show how `UserService` and `OrderService` both get the same instance

<details>
<summary>🔍 Hint 1</summary>
What if the constructor was private so nobody could call `new AppConfig()`?
</details>

<details>
<summary>🔍 Hint 2</summary>
The class needs to create its own instance and hand it out. A static method or field could hold it.
</details>

<details>
<summary>✅ Pattern Reveal</summary>

**Pattern: Singleton**

Why: You need exactly one instance shared across the entire application. The instance is expensive to create (disk read), so creating it multiple times is wasteful and inconsistent.

</details>

### Starter Code

```java
public class AppConfig {
    private int maxRetryCount;
    private int timeoutSeconds;
    private String environment;

    // TODO: make this a Singleton
    // Step 1: make the constructor private
    // Step 2: hold the single instance in a static field
    // Step 3: provide a static getInstance() method
    // Step 4: load config in the constructor (simulate with Thread.sleep(200))

    private AppConfig() {
        // simulate slow loading
        System.out.println("Loading config from file...");
        this.maxRetryCount = 3;
        this.timeoutSeconds = 30;
        this.environment = "production";
    }

    // TODO: add getInstance() here

    public int getMaxRetryCount() { return maxRetryCount; }
    public int getTimeoutSeconds() { return timeoutSeconds; }
    public String getEnvironment() { return environment; }
}

// Test it:
class Main {
    public static void main(String[] args) {
        AppConfig c1 = AppConfig.getInstance();
        AppConfig c2 = AppConfig.getInstance();

        // This must print TRUE — both references point to the same object
        System.out.println("Same instance: " + (c1 == c2));

        // Config loaded message should print ONCE, not twice
    }
}
```

### Expected Output
```
Loading config from file...
Same instance: true
```

---

## Problem 2: The Connection Pool

### Scenario
Your app connects to a database. Creating a new connection is expensive — it involves network handshake, authentication, and resource allocation.

You decide to maintain a **pool** of 10 pre-created connections that get reused.

The problem: if `UserService` creates its own pool of 10 and `OrderService` creates another pool of 10, you now have 20 connections open — your database only allows 15.

**How do you ensure there is only ONE connection pool shared by the entire app?**

### Your Task
1. Which pattern solves this?
2. Implement `ConnectionPool` — use the **enum Singleton** approach (best for production)
3. `getConnection()` returns a connection from the pool; `releaseConnection()` returns it

<details>
<summary>🔍 Hint</summary>
Enum in Java guarantees a single instance even under reflection and serialization. Each enum constant is exactly one object.
</details>

<details>
<summary>✅ Pattern Reveal</summary>

**Pattern: Singleton (Enum variant)**

Why: Connection pool must be global. Enum Singleton is thread-safe, serialization-safe, and reflection-safe — the production-grade choice.

</details>

### Starter Code

```java
import java.util.ArrayDeque;
import java.util.Queue;

// TODO: Convert this to an Enum Singleton
public class ConnectionPool {
    private static final int POOL_SIZE = 10;
    private final Queue<String> connections = new ArrayDeque<>();

    // Simulate creating connections
    private ConnectionPool() {
        for (int i = 1; i <= POOL_SIZE; i++) {
            connections.add("Connection-" + i);
        }
        System.out.println("Pool created with " + POOL_SIZE + " connections");
    }

    public String getConnection() {
        return connections.poll();  // returns null if pool is empty
    }

    public void releaseConnection(String conn) {
        connections.offer(conn);
    }

    public int availableConnections() {
        return connections.size();
    }
}

// Test:
class Main {
    public static void main(String[] args) {
        // Both services get the SAME pool
        ConnectionPool pool1 = ConnectionPool.INSTANCE; // after converting to enum
        ConnectionPool pool2 = ConnectionPool.INSTANCE;

        System.out.println("Same pool: " + (pool1 == pool2));  // must be true

        String conn = pool1.getConnection();
        System.out.println("Got: " + conn);
        System.out.println("Remaining: " + pool2.availableConnections());  // must be 9
    }
}
```

---

## Problem 3: Thread-Safe Logger

### Scenario
You're writing a Logger that appends messages to a single log file. 

Your app has 50 threads running simultaneously. Each thread calls `Logger.log("some message")`.

If each thread creates its own `Logger` instance, they'll all try to write to the file at the same time → corrupted file.

You need a single Logger instance, but it must also be **thread-safe** (two threads calling `log()` simultaneously should not corrupt output).

### Your Task
1. Implement a thread-safe Singleton Logger using **double-checked locking**
2. The `log()` method must be synchronized so only one thread writes at a time
3. Demonstrate two threads logging without corruption

<details>
<summary>🔍 Hint — Why double-checked locking?</summary>

If you synchronize the entire `getInstance()` method, every call acquires a lock — even after the instance is created. Double-checked locking only locks during the FIRST creation.

```java
if (instance == null) {           // check 1: no lock (fast path)
    synchronized(Logger.class) {
        if (instance == null) {   // check 2: inside lock (safe)
            instance = new Logger();
        }
    }
}
```

The `volatile` keyword on the instance field is REQUIRED — without it, the CPU can publish the reference before the constructor finishes.

</details>

<details>
<summary>✅ Pattern Reveal</summary>

**Pattern: Singleton (Double-Checked Locking)**

Why: Thread-safe lazy initialization. Avoids locking on every call once the instance is created.

</details>

### Starter Code

```java
import java.time.Instant;

public class Logger {
    // TODO: make this volatile and static
    private static Logger instance;

    private Logger() {
        System.out.println("Logger initialized");
    }

    // TODO: implement thread-safe getInstance() with double-checked locking
    public static Logger getInstance() {
        // your implementation here
        return instance;
    }

    // Thread-safe log method
    public synchronized void log(String message) {
        System.out.println("[" + Instant.now() + "] [" + Thread.currentThread().getName() + "] " + message);
    }
}

// Test with multiple threads:
class Main {
    public static void main(String[] args) throws InterruptedException {
        Runnable task = () -> {
            Logger logger = Logger.getInstance();
            logger.log("Message from " + Thread.currentThread().getName());
        };

        Thread t1 = new Thread(task, "Thread-1");
        Thread t2 = new Thread(task, "Thread-2");
        Thread t3 = new Thread(task, "Thread-3");

        t1.start(); t2.start(); t3.start();
        t1.join();  t2.join();  t3.join();

        // "Logger initialized" must print EXACTLY ONCE
    }
}
```

---

## 📖 Double-Checked Locking — Reference Guide

> Use this when you need **lazy + thread-safe** Singleton initialization.

### The Problem It Solves

Without synchronization, two threads can both see `instance == null` at the same time and both create a new instance → Singleton broken.

Synchronizing the entire `getInstance()` method works but is slow — every call acquires a lock even after the instance is created.

### The Pattern

```java
private static volatile MyClass instance;  // volatile is REQUIRED

public static MyClass getInstance() {
    if (instance == null) {                    // Check 1 — no lock, fast path
        synchronized (MyClass.class) {         // Lock acquired only during first creation
            if (instance == null) {            // Check 2 — inside lock, safe
                instance = new MyClass();      // Create ONLY here
            }
        }
    }
    return instance;
}
```

### Why Two Checks?

| Check | Purpose |
|-------|---------|
| Outer `if (instance == null)` | Fast path — skips lock entirely after instance is created (99.99% of calls) |
| Inner `if (instance == null)` | Safety net — another thread may have created it while we waited for the lock |

### Why `volatile` Is Required

Without `volatile`, the CPU can **reorder instructions**. Object creation has 3 steps:
1. Allocate memory
2. Run constructor
3. Assign reference to `instance`

CPU can reorder to: **1 → 3 → 2**

Thread 2 sees `instance != null` (step 3 done) but the constructor hasn't finished (step 2 pending) → Thread 2 uses a **half-constructed object** → silent bug.

`volatile` prevents reordering — guarantees the reference is only visible after the constructor completes.

### All 5 Singleton Implementations — Quick Reference

| Implementation | Lazy | Thread-Safe | Notes |
|---------------|------|-------------|-------|
| Eager (`private static final`) | ❌ | ✅ | Simplest. Use when init is cheap. |
| Synchronized method | ✅ | ✅ | Safe but slow — locks on every call. |
| Double-Checked Locking | ✅ | ✅ | Production standard for expensive lazy init. `volatile` required. |
| Static Inner Class (Holder) | ✅ | ✅ | Elegant. JVM guarantees thread safety via class loading. |
| Enum | ✅ | ✅ | **Interview favorite.** Reflection-safe + serialization-safe. |

### When to Use Which

| Scenario | Use |
|----------|-----|
| Config loaded at startup | Eager |
| Logger shared across app | DCL or Enum |
| Connection pool | Enum |
| Cache (expensive to init) | DCL or Static Inner Class |
| Any Singleton in Java | Enum (safest default) |

---

### Eager vs Lazy Initialization — Key Difference

| | Eager | Lazy |
|--|-------|------|
| **When created** | At class load time (JVM startup) | On first call to `getInstance()` |
| **Thread safety** | ✅ Always safe — JVM guarantees class loading is atomic | ⚠️ Need extra care — use DCL or Static Inner Class |
| **Memory** | Instance exists even if never used | Instance only created when needed |
| **Startup time** | Slower — all eager singletons init at startup | Faster startup — defers cost |
| **Use when** | Must be ready before first request (config, bootstrap) | Optional or expensive — init only if needed (cache, logger) |

**Code comparison:**

```java
// EAGER — simple, always safe
private static final AppConfig INSTANCE = new AppConfig();  // created at class load

// LAZY (DCL) — deferred until first use
private static volatile AppConfig INSTANCE;  // null until getInstance() called

public static AppConfig getInstance() {
    if (INSTANCE == null) {
        synchronized (AppConfig.class) {
            if (INSTANCE == null) INSTANCE = new AppConfig();
        }
    }
    return INSTANCE;
}
```

**Decision rule:**
> "Will the app BREAK if this isn't initialized at startup?" → **Eager**
> "Is this only needed sometimes, or expensive to create?" → **Lazy (DCL or Enum)**

**Real examples:**
- `AppConfig` — Eager (services need config before first request)
- `Logger` — Lazy DCL (logging is optional, init on first log call)
- `ConnectionPool` — Enum (thread-safe, init once, always needed)
- `ReportCache` — Lazy (cache only needed if reports are requested)

---

## Problem 4: The Anti-Singleton — When NOT to Use It

### Scenario
A junior developer on your team wrote this:

```java
@Singleton  // custom annotation
public class UserPreferences {
    private Map<String, String> preferences = new HashMap<>();

    public void set(String key, String value) { preferences.put(key, value); }
    public String get(String key)             { return preferences.get(key); }
}
```

They made `UserPreferences` a Singleton because "preferences are loaded once."

In your system, 100 concurrent users each have DIFFERENT preferences (dark mode, language, notification settings).

**What's wrong? How do you fix it?**

### Your Task
1. Identify why Singleton is WRONG here
2. Explain what actually happens with 100 concurrent users sharing one instance
3. Propose the correct design (no Singleton)

<details>
<summary>✅ Answer Reveal</summary>

**Anti-Pattern: Singleton for USER-SCOPED data**

**What goes wrong:**
- User A sets `preferences.set("theme", "dark")`
- User B sets `preferences.set("theme", "light")` — overwrites User A's preference!
- Both users see the SAME preferences map → User A now has light theme

**The fix:** `UserPreferences` should NOT be a Singleton. It should be:
- A regular object — one instance per user session
- Stored in the user's session/cache by user ID
- Or a record: `record UserPreferences(String theme, String language) {}`

**Rule:** Singleton is for SHARED GLOBAL state (config, connection pool, logger).
NEVER use Singleton for PER-USER or PER-REQUEST state.

</details>
