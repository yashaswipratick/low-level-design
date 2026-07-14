# Proxy — Practice Problems
> Goal: Control access to another object — for lazy loading, security checks, or caching.

---

## Key Intuition
**Proxy = a stand-in or gatekeeper.** The client thinks it's talking to the real object. The proxy intercepts the call and decides:
- Should I load the real object now? (Virtual Proxy)
- Does this caller have permission? (Protection Proxy)
- Do I have this result cached? (Caching Proxy)

---

## Problem 1: Lazy Image Loading (Virtual Proxy)

### Scenario
You're building a photo gallery. Each image loads from disk (takes ~2 seconds).

The gallery shows 100 thumbnails. If you load ALL 100 images when the page loads, the user waits 200 seconds. That's terrible.

**Better:** Show a placeholder initially. Load the actual image ONLY when the user scrolls to it and it becomes visible.

A junior dev suggests:
```java
// Just load all images upfront
List<Image> images = files.stream().map(f -> new DiskImage(f)).toList();
// Page loads in 200 seconds...
```

**How do you defer the expensive loading until the moment the image is actually needed?**

### Your Task
1. `Image` interface: `void display()`
2. `DiskImage` — real implementation (simulates 2-second load in constructor)
3. `ImageProxy` — stands in for `DiskImage`; only creates it when `display()` is first called

<details>
<summary>🔍 Hint</summary>

`ImageProxy` holds:
- The filename (to create `DiskImage` when needed)
- A `DiskImage` reference (starts as `null`)

On `display()`:
- If `diskImage == null` → create it now (expensive, one-time)
- Delegate `display()` to the real image
</details>

<details>
<summary>✅ Pattern Reveal</summary>

**Pattern: Proxy (Virtual / Lazy Loading Proxy)**

Why: The real object is expensive to create. A proxy stands in its place. The real object is only created when it's actually needed — not before.

</details>

### Starter Code

```java
// Component interface
public interface Image {
    void display();
}

// Real object — expensive to create
public class DiskImage implements Image {
    private final String filename;

    public DiskImage(String filename) {
        this.filename = filename;
        // Simulate expensive disk load
        System.out.println("Loading image from disk: " + filename + " [~2 seconds]");
        try { Thread.sleep(100); } catch (InterruptedException e) { }  // simulated delay
    }

    @Override
    public void display() {
        System.out.println("Displaying: " + filename);
    }
}

// Proxy — defers creation of DiskImage
public class ImageProxy implements Image {
    private final String filename;
    private DiskImage realImage;  // starts as null — not loaded yet

    public ImageProxy(String filename) {
        this.filename = filename;
        // NO disk load here — just stores the filename
        System.out.println("Proxy created for: " + filename + " (not loaded yet)");
    }

    @Override
    public void display() {
        // TODO: only load the real image on first display() call
        if (realImage == null) {
            realImage = new DiskImage(filename);  // expensive — only once
        }
        realImage.display();
    }
}

// Test:
class Main {
    public static void main(String[] args) {
        // Create 3 proxies instantly (no disk load yet)
        Image img1 = new ImageProxy("photo1.jpg");
        Image img2 = new ImageProxy("photo2.jpg");
        Image img3 = new ImageProxy("photo3.jpg");
        System.out.println("Gallery initialized (no images loaded)\n");

        // User scrolls to image 1 — load only now
        img1.display();
        System.out.println();

        // User scrolls to image 1 again — no reload!
        img1.display();
        System.out.println();

        // User never scrolls to image 3 — never loaded
    }
}
```

### Expected Output
```
Proxy created for: photo1.jpg (not loaded yet)
Proxy created for: photo2.jpg (not loaded yet)
Proxy created for: photo3.jpg (not loaded yet)
Gallery initialized (no images loaded)

Loading image from disk: photo1.jpg [~2 seconds]
Displaying: photo1.jpg

Displaying: photo1.jpg        ← second call: no reload
```

---

## Problem 2: Access Control (Protection Proxy)

### Scenario
You have a `BankAccount` service:

```java
public interface BankAccount {
    void deposit(double amount);
    void withdraw(double amount);
    double getBalance();
}
```

Your bank has a rule: **only the account OWNER and ADMINS can see the balance or withdraw money**. Others can deposit (gifts), but nothing else.

You want to add this security WITHOUT touching `RealBankAccount`.

### Your Task
1. `RealBankAccount` — implements `BankAccount` (no security logic)
2. `BankAccountProxy` — implements `BankAccount`, checks permissions before delegating
3. If user is not authorized: throw `AccessDeniedException` (for `getBalance`, `withdraw`)

<details>
<summary>✅ Pattern Reveal</summary>

**Pattern: Proxy (Protection Proxy)**

Why: You want to add access control to an existing object without modifying it. The proxy intercepts every call and decides: is this caller allowed?

</details>

### Starter Code

```java
public interface BankAccount {
    void deposit(double amount);
    void withdraw(double amount);
    double getBalance();
}

public class RealBankAccount implements BankAccount {
    private double balance;
    private final String ownerId;

    public RealBankAccount(String ownerId, double initialBalance) {
        this.ownerId = ownerId;
        this.balance = initialBalance;
    }

    @Override
    public void deposit(double amount) {
        balance += amount;
        System.out.println("Deposited ₹" + amount + " | Balance: ₹" + balance);
    }

    @Override
    public void withdraw(double amount) {
        if (amount > balance) throw new IllegalArgumentException("Insufficient funds");
        balance -= amount;
        System.out.println("Withdrew ₹" + amount + " | Balance: ₹" + balance);
    }

    @Override
    public double getBalance() { return balance; }
}

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(String action) {
        super("Access denied: not authorized to perform '" + action + "'");
    }
}

// Protection Proxy
public class BankAccountProxy implements BankAccount {
    private final RealBankAccount realAccount;
    private final String currentUserId;
    private final String ownerId;

    public BankAccountProxy(RealBankAccount account, String currentUserId, String ownerId) {
        this.realAccount = account;
        this.currentUserId = currentUserId;
        this.ownerId = ownerId;
    }

    private boolean isAuthorized() {
        return currentUserId.equals(ownerId) || currentUserId.equals("ADMIN");
    }

    @Override
    public void deposit(double amount) {
        realAccount.deposit(amount);  // anyone can deposit
    }

    @Override
    public void withdraw(double amount) {
        // TODO: check authorization before allowing withdraw
        if (!isAuthorized()) throw new AccessDeniedException("withdraw");
        realAccount.withdraw(amount);
    }

    @Override
    public double getBalance() {
        // TODO: check authorization before allowing balance view
        if (!isAuthorized()) throw new AccessDeniedException("getBalance");
        return realAccount.getBalance();
    }
}

// Test:
class Main {
    public static void main(String[] args) {
        RealBankAccount account = new RealBankAccount("alice", 1000.0);

        BankAccount aliceView = new BankAccountProxy(account, "alice", "alice");
        BankAccount bobView   = new BankAccountProxy(account, "bob", "alice");
        BankAccount adminView = new BankAccountProxy(account, "ADMIN", "alice");

        aliceView.deposit(500);      // ✅ anyone can deposit
        System.out.println(aliceView.getBalance());  // ✅ alice can see balance

        bobView.deposit(100);        // ✅ bob can deposit

        try {
            System.out.println(bobView.getBalance());  // ❌ bob cannot see balance
        } catch (AccessDeniedException e) {
            System.out.println(e.getMessage());
        }

        System.out.println(adminView.getBalance());  // ✅ admin can see balance
    }
}
```

---

## Problem 3: Caching Proxy

### Scenario
Fetching a user profile from the database takes 500ms. User profiles rarely change. The same profile is requested 50 times per minute.

You want to cache the result: first call fetches from DB (500ms), subsequent calls return the cached result instantly.

### Your Task
1. `UserProfileService` interface: `UserProfile getProfile(String userId)`
2. `DatabaseUserProfileService` — real implementation (slow, simulates 500ms)
3. `CachingUserProfileProxy` — caches results in a `Map`; returns cached value on repeat calls

<details>
<summary>✅ Pattern Reveal</summary>

**Pattern: Proxy (Caching Proxy)**

Why: Expensive operation should happen only once per unique input. The proxy intercepts, checks the cache, and only calls the real service on a cache miss.

</details>

### Starter Code

```java
import java.util.HashMap;
import java.util.Map;

public record UserProfile(String userId, String name, String email) {}

public interface UserProfileService {
    UserProfile getProfile(String userId);
}

public class DatabaseUserProfileService implements UserProfileService {
    @Override
    public UserProfile getProfile(String userId) {
        // Simulate slow DB query
        System.out.println("DB QUERY for userId: " + userId + " [500ms]");
        try { Thread.sleep(100); } catch (InterruptedException e) { }
        return new UserProfile(userId, "User-" + userId, userId + "@example.com");
    }
}

public class CachingUserProfileProxy implements UserProfileService {
    private final UserProfileService realService;
    private final Map<String, UserProfile> cache = new HashMap<>();

    public CachingUserProfileProxy(UserProfileService service) {
        this.realService = service;
    }

    @Override
    public UserProfile getProfile(String userId) {
        // TODO: return from cache if available; otherwise call real service and cache result
        if (cache.containsKey(userId)) {
            System.out.println("CACHE HIT for: " + userId);
            return cache.get(userId);
        }
        UserProfile profile = realService.getProfile(userId);
        cache.put(userId, profile);
        return profile;
    }
}

// Test:
class Main {
    public static void main(String[] args) {
        UserProfileService service = new CachingUserProfileProxy(new DatabaseUserProfileService());

        service.getProfile("alice");  // DB QUERY (slow)
        service.getProfile("alice");  // CACHE HIT (instant)
        service.getProfile("alice");  // CACHE HIT (instant)
        service.getProfile("bob");    // DB QUERY (slow)
        service.getProfile("bob");    // CACHE HIT (instant)
    }
}
```
