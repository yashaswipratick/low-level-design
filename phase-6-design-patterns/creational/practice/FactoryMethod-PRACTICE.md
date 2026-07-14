# Factory Method — Practice Problems
> Goal: Recognize when object creation logic needs to be delegated to subclasses.

---

## Problem 1: Notification Sender

### Scenario
You're building a notification service. Depending on user preference, you need to send either an **Email**, **SMS**, or **Push Notification**.

You have a `NotificationService` class with a `sendWelcome(User user)` method. The body of `sendWelcome` is always the same — greet the user, include their name, add a CTA button. The ONLY thing that changes is HOW it gets delivered.

A junior dev wrote this:

```java
public void sendWelcome(User user, String type) {
    if (type.equals("EMAIL")) {
        EmailSender sender = new EmailSender();
        sender.send(user.email(), "Welcome " + user.name());
    } else if (type.equals("SMS")) {
        SmsSender sender = new SmsSender();
        sender.send(user.phone(), "Welcome " + user.name());
    }
    // Adding PUSH requires modifying this method
}
```

**Every time a new channel is added, someone edits this method. How do you fix it?**

### Your Task
1. Which pattern solves this?
2. Create a `NotificationSender` interface with `send(User user, String message)`
3. Create `EmailSender`, `SmsSender`, `PushSender` implementations
4. Create a Factory that returns the right sender based on user preference

<details>
<summary>🔍 Hint 1</summary>
The key problem: the caller decides which class to instantiate. What if the caller didn't need to know?
</details>

<details>
<summary>🔍 Hint 2</summary>
What if there was a method `createSender(String type)` that returns the right object? The caller just calls `send()` without knowing what's behind it.
</details>

<details>
<summary>✅ Pattern Reveal</summary>

**Pattern: Factory Method (or Simple Factory)**

Why: The type of object to create is decided at runtime based on user preference. The client code (`sendWelcome`) shouldn't be coupled to concrete `EmailSender`, `SmsSender` classes.

</details>

### Starter Code

```java
// Step 1: Define the product interface
public interface NotificationSender {
    void send(String recipient, String message);
}

// Step 2: Create concrete products
public class EmailSender implements NotificationSender {
    @Override
    public void send(String recipient, String message) {
        System.out.println("EMAIL to " + recipient + ": " + message);
    }
}

// TODO: implement SmsSender and PushSender

// Step 3: Create the factory
public class NotificationSenderFactory {
    // TODO: implement this method
    public static NotificationSender create(String type) {
        // return the right sender based on type
        return null;
    }
}

// Step 4: Use it in the service
public class NotificationService {
    public void sendWelcome(User user) {
        NotificationSender sender = NotificationSenderFactory.create(user.preferredChannel());
        sender.send(user.contactInfo(), "Welcome, " + user.name() + "!");
    }
}

// Test:
// Adding WhatsApp should require ZERO changes to NotificationService
```

---

## Problem 2: Document Exporter

### Scenario
You're building a reporting tool. Users can export reports as **PDF**, **Excel (XLSX)**, or **CSV**.

The export process is always:
1. Fetch data
2. Format it
3. Write to output stream

But how you "format" and "write" is completely different per file type.

**Design a system where adding a new format (e.g., XML) requires only adding ONE new class — not touching the existing export logic.**

### Your Task
1. Define a `DocumentExporter` interface with `export(ReportData data, OutputStream out)`
2. Create `PdfExporter`, `ExcelExporter`, `CsvExporter`
3. Create a factory that maps file extension → exporter
4. The calling code should work like: `exporterFactory.create("pdf").export(data, out)`

<details>
<summary>✅ Pattern Reveal</summary>

**Pattern: Factory Method / Simple Factory**

The factory decouples the client from the concrete exporter classes. Adding XML exporter = add `XmlExporter` class + register in factory. Zero changes to existing code.

</details>

### Starter Code

```java
public interface DocumentExporter {
    void export(String data, java.io.OutputStream out) throws Exception;
    String supportedFormat(); // returns "pdf", "xlsx", "csv"
}

public class CsvExporter implements DocumentExporter {
    @Override
    public void export(String data, java.io.OutputStream out) throws Exception {
        // Simulate: just write the data as-is (CSV is plain text)
        out.write(("CSV:\n" + data).getBytes());
    }

    @Override
    public String supportedFormat() { return "csv"; }
}

// TODO: implement PdfExporter and ExcelExporter (can be simulated with print statements)

public class ExporterFactory {
    private static final Map<String, DocumentExporter> exporters = new HashMap<>();

    static {
        // TODO: register all exporters
    }

    public static DocumentExporter create(String format) {
        DocumentExporter exporter = exporters.get(format.toLowerCase());
        if (exporter == null) throw new IllegalArgumentException("Unknown format: " + format);
        return exporter;
    }
}

// Test:
// ExporterFactory.create("csv").export("name,age\nAlice,30", System.out);
// ExporterFactory.create("pdf").export("name,age\nAlice,30", System.out);
```

---

## Problem 3: Game Character Creator

### Scenario
You're building an RPG game. Players choose a character class: **Warrior**, **Mage**, or **Archer**. Each character class has different:
- Starting health (Warrior=150, Mage=80, Archer=100)
- Attack style (`slash()`, `cast()`, `shoot()`)
- Starting weapon ("Sword", "Staff", "Bow")

The character selection screen just passes a string like `"WARRIOR"` and expects back a fully initialized `Character` object.

**How do you design the creation so adding "Paladin" class is easy?**

### Your Task
1. Create `Character` abstract class with `attack()`, `getHealth()`, `getWeapon()`
2. Implement `Warrior`, `Mage`, `Archer` as concrete classes
3. Create `CharacterFactory` with `createCharacter(String type)` method

<details>
<summary>🔍 Hint</summary>

The Factory Method pattern has a `createProduct()` method that subclasses override. In Simple Factory, there's just one factory class with a switch/map. For beginners, start with Simple Factory — it's the easier form.

</details>

<details>
<summary>✅ Pattern Reveal</summary>

**Pattern: Factory Method (or Simple Factory)**

Why: The exact `Character` subtype to instantiate is decided at runtime (user choice). The factory centralizes this decision — the game engine just calls `createCharacter("WARRIOR")` without importing `Warrior.class` directly.

</details>

### Starter Code

```java
public abstract class Character {
    protected int health;
    protected String weapon;
    protected String name;

    public abstract void attack();

    public int getHealth()   { return health; }
    public String getWeapon() { return weapon; }
    public String getName()   { return name; }
}

public class Warrior extends Character {
    public Warrior() {
        this.health = 150;
        this.weapon = "Sword";
        this.name = "Warrior";
    }

    @Override
    public void attack() {
        System.out.println("Warrior slashes with " + weapon + "!");
    }
}

// TODO: implement Mage (health=80, weapon="Staff") and Archer (health=100, weapon="Bow")

public class CharacterFactory {
    public static Character createCharacter(String type) {
        // TODO: return the right character based on type string
        return null;
    }
}

// Test:
// Character c = CharacterFactory.createCharacter("MAGE");
// System.out.println(c.getName() + " | HP: " + c.getHealth());
// c.attack();
```

### Expected Output
```
Mage | HP: 80
Mage casts a spell with Staff!
```
