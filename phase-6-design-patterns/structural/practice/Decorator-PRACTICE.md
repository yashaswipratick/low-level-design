# Decorator — Practice Problems
> Goal: Add behaviors to objects DYNAMICALLY without subclassing. Stack them like Russian dolls.

---

## Key Intuition
**Decorator = wrapping paper.** You have a Gift (the real object). You wrap it with Ribbon (decorator). Then Bow (another decorator). The gift is still a gift — but now it has ribbon AND bow on top.

Each decorator:
1. Implements the SAME interface as the original
2. HOLDS a reference to the original (or another decorator)
3. Adds behavior BEFORE or AFTER delegating to the wrapped object

---

## Problem 1: Coffee Shop

### Scenario
You run a coffee shop. Every drink starts as a base coffee with a cost and description:
- `SimpleCoffee` — costs ₹50, description: "Simple coffee"

Customers can add extras:
- **Milk** — +₹20, adds "Milk" to description
- **Sugar** — +₹10, adds "Sugar" to description  
- **Vanilla** — +₹30, adds "Vanilla" to description

A customer orders: "Coffee with milk and two sugars"
```
Cost = 50 + 20 + 10 + 10 = ₹90
Description = "Simple coffee, Milk, Sugar, Sugar"
```

A junior dev tries to solve this with inheritance:
```java
class CoffeeWithMilk extends Coffee { }
class CoffeeWithMilkAndSugar extends CoffeeWithMilk { }
class CoffeeWithMilkAndSugarAndVanilla extends CoffeeWithMilkAndSugar { }
// 10 ingredients → 2^10 = 1024 subclasses!
```

**How do you combine 10 toppings without 1024 classes?**

### Your Task
1. Which pattern solves this? Why?
2. Create `Coffee` interface with `double getCost()` and `String getDescription()`
3. Implement `SimpleCoffee` as the base
4. Create abstract `CoffeeDecorator` that wraps a `Coffee`
5. Implement `MilkDecorator`, `SugarDecorator`, `VanillaDecorator`

<details>
<summary>🔍 Hint 1</summary>
Each decorator implements `Coffee` AND holds a `Coffee` inside it. When you call `getCost()` on the decorator, it returns `wrappedCoffee.getCost() + myExtraCost`.
</details>

<details>
<summary>🔍 Hint 2</summary>
The wrapping chain:
```
VanillaDecorator
  └── MilkDecorator
        └── SimpleCoffee
```
Calling `getCost()` on VanillaDecorator = 30 + MilkDecorator.getCost() = 30 + (20 + SimpleCoffee.getCost()) = 30+20+50 = ₹100
</details>

<details>
<summary>✅ Pattern Reveal</summary>

**Pattern: Decorator**

Why: You want to add behaviors at runtime in any combination. Inheritance would require one class per combination. Decorator wraps and stacks — each decorator adds exactly one behavior.

</details>

### Starter Code

```java
// Component interface
public interface Coffee {
    double getCost();
    String getDescription();
}

// Concrete component
public class SimpleCoffee implements Coffee {
    @Override
    public double getCost() { return 50.0; }

    @Override
    public String getDescription() { return "Simple coffee"; }
}

// Abstract Decorator — holds the wrapped Coffee
public abstract class CoffeeDecorator implements Coffee {
    protected final Coffee decoratedCoffee;

    public CoffeeDecorator(Coffee coffee) {
        this.decoratedCoffee = coffee;
    }

    @Override
    public double getCost() { return decoratedCoffee.getCost(); }

    @Override
    public String getDescription() { return decoratedCoffee.getDescription(); }
}

// Concrete Decorators
public class MilkDecorator extends CoffeeDecorator {
    public MilkDecorator(Coffee coffee) { super(coffee); }

    @Override
    public double getCost() {
        return super.getCost() + 20;  // adds milk cost
    }

    @Override
    public String getDescription() {
        return super.getDescription() + ", Milk";  // adds to description
    }
}

// TODO: implement SugarDecorator (+₹10) and VanillaDecorator (+₹30)

// Test:
class Main {
    public static void main(String[] args) {
        // Coffee with milk and two sugars
        Coffee coffee = new SimpleCoffee();
        coffee = new MilkDecorator(coffee);
        coffee = new SugarDecorator(coffee);
        coffee = new SugarDecorator(coffee);  // two sugars!

        System.out.println(coffee.getDescription());  // Simple coffee, Milk, Sugar, Sugar
        System.out.println("Cost: ₹" + coffee.getCost());  // ₹90.0

        // Coffee with vanilla and milk
        Coffee fancyCoffee = new VanillaDecorator(new MilkDecorator(new SimpleCoffee()));
        System.out.println(fancyCoffee.getDescription());
        System.out.println("Cost: ₹" + fancyCoffee.getCost());  // ₹100.0
    }
}
```

---

## Problem 2: Text Formatter

### Scenario
You're building a text editor. Text can be formatted:
- **Bold** → wraps text in `**text**`
- **Italic** → wraps text in `_text_`
- **Underline** → wraps text in `<u>text</u>`

A user selects "Bold + Italic" → result: `**_Hello_**`
A user selects "Bold + Italic + Underline" → result: `**_<u>Hello</u>_**`

Combinations can be applied in any order and any number of times.

### Your Task
1. `TextFormatter` interface with `String format(String text)`
2. `PlainText` base implementation (returns text as-is)
3. `BoldDecorator`, `ItalicDecorator`, `UnderlineDecorator`

### Starter Code

```java
public interface TextFormatter {
    String format(String text);
}

public class PlainText implements TextFormatter {
    @Override
    public String format(String text) { return text; }
}

public abstract class TextDecorator implements TextFormatter {
    protected final TextFormatter wrapped;
    public TextDecorator(TextFormatter wrapped) { this.wrapped = wrapped; }
}

public class BoldDecorator extends TextDecorator {
    public BoldDecorator(TextFormatter f) { super(f); }

    @Override
    public String format(String text) {
        return "**" + wrapped.format(text) + "**";
    }
}

// TODO: ItalicDecorator → "_" + text + "_"
// TODO: UnderlineDecorator → "<u>" + text + "</u>"

class Main {
    public static void main(String[] args) {
        TextFormatter bold       = new BoldDecorator(new PlainText());
        TextFormatter boldItalic = new ItalicDecorator(new BoldDecorator(new PlainText()));

        System.out.println(bold.format("Hello"));            // **Hello**
        System.out.println(boldItalic.format("Hello"));      // **_Hello_**

        // Bold + Italic + Underline
        TextFormatter all = new BoldDecorator(new ItalicDecorator(new UnderlineDecorator(new PlainText())));
        System.out.println(all.format("Hello"));             // **_<u>Hello</u>_**
    }
}
```

---

## Problem 3: Notification Channel with Deduplication and Rate Limiting

### Scenario
Your notification channel has these possible wrappings:
- **RetryDecorator** — on failure, retries up to 3 times
- **LoggingDecorator** — logs every send attempt
- **RateLimitDecorator** — allows max 5 sends per minute per user

Any channel (Email, SMS, Push) can be wrapped with any combination of these decorators.

### Your Task
1. `NotificationChannel` interface: `void send(String userId, String message)`
2. `EmailChannel` base implementation
3. `LoggingDecorator`, `RetryDecorator` (retries on exception), `RateLimitDecorator`

### Starter Code

```java
public interface NotificationChannel {
    void send(String userId, String message);
}

public class EmailChannel implements NotificationChannel {
    @Override
    public void send(String userId, String message) {
        System.out.println("EMAIL → " + userId + ": " + message);
        // Simulate occasional failure:
        if (Math.random() < 0.3) throw new RuntimeException("Email server unavailable");
    }
}

public abstract class ChannelDecorator implements NotificationChannel {
    protected final NotificationChannel wrapped;
    protected ChannelDecorator(NotificationChannel wrapped) { this.wrapped = wrapped; }
}

public class LoggingDecorator extends ChannelDecorator {
    public LoggingDecorator(NotificationChannel channel) { super(channel); }

    @Override
    public void send(String userId, String message) {
        System.out.println("[LOG] Sending to " + userId);
        wrapped.send(userId, message);
        System.out.println("[LOG] Sent successfully to " + userId);
    }
}

public class RetryDecorator extends ChannelDecorator {
    private static final int MAX_RETRIES = 3;

    public RetryDecorator(NotificationChannel channel) { super(channel); }

    @Override
    public void send(String userId, String message) {
        // TODO: try up to MAX_RETRIES times; if all fail, log and give up
        int attempts = 0;
        while (attempts < MAX_RETRIES) {
            try {
                wrapped.send(userId, message);
                return;  // success
            } catch (Exception e) {
                attempts++;
                System.out.println("[RETRY] Attempt " + attempts + " failed: " + e.getMessage());
            }
        }
        System.out.println("[RETRY] All " + MAX_RETRIES + " attempts failed for " + userId);
    }
}

// TODO: RateLimitDecorator — track sends per userId per minute; throw if exceeded

// Test:
// NotificationChannel channel = new LoggingDecorator(new RetryDecorator(new EmailChannel()));
// channel.send("user123", "Your order shipped!");
```

---

## Problem 4: File Reader Pipeline

### Scenario
You need to read a file. But sometimes the file is:
- **Compressed** (need to decompress first)
- **Encrypted** (need to decrypt first)  
- **Buffered** (improve read performance)

These can be stacked in any order. Just like Java's own:
```java
new BufferedReader(new InputStreamReader(new FileInputStream("file.txt")))
```

### Your Task
1. `DataReader` interface: `String read()`
2. `FileDataReader` base (returns raw file content)
3. `BufferedReaderDecorator` (simulates buffering — just adds prefix "BUFFERED: ")
4. `CompressionDecorator` (simulates decompression)
5. `EncryptionDecorator` (simulates decryption — just reverses the string)

```java
public interface DataReader {
    String read();
}

public class FileDataReader implements DataReader {
    private final String filename;
    public FileDataReader(String filename) { this.filename = filename; }

    @Override
    public String read() {
        // Simulate reading encrypted compressed file
        return "detpyrcne-desserpmoC:eliF";  // encrypted + compressed content
    }
}

public abstract class DataReaderDecorator implements DataReader {
    protected final DataReader wrapped;
    public DataReaderDecorator(DataReader r) { this.wrapped = r; }
}

// EncryptionDecorator: simulates decrypt by reversing the string
public class EncryptionDecorator extends DataReaderDecorator {
    public EncryptionDecorator(DataReader r) { super(r); }

    @Override
    public String read() {
        String encrypted = wrapped.read();
        return new StringBuilder(encrypted).reverse().toString();  // "decrypt"
    }
}

// TODO: CompressionDecorator — removes "Compressed:" prefix
// TODO: BufferedReaderDecorator — adds "BUFFERED:" prefix

// Test:
// DataReader reader = new BufferedReaderDecorator(
//     new CompressionDecorator(
//         new EncryptionDecorator(
//             new FileDataReader("data.txt")
//         )
//     )
// );
// System.out.println(reader.read());  // BUFFERED: File content
```
