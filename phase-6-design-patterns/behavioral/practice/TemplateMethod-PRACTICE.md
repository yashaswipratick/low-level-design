# Template Method — Practice Problems
> Goal: Define a fixed SKELETON of an algorithm; let subclasses fill in the steps.

---

## Key Intuition
**Template Method = recipe card.** "To make a hot drink: 1) Boil water 2) Brew the drink 3) Pour in cup 4) Add condiments." Steps 1 and 3 are the same for all drinks. Step 2 (brew) and step 4 (condiments) differ: coffee vs tea. The TEMPLATE is the same; the DETAILS vary.

**When to think Template Method:** Multiple classes share the SAME algorithm structure but differ in specific steps.

---

## Problem 1: Data Parser Pipeline

### Scenario
You process reports from three sources: **CSV files**, **XML files**, **JSON files**.

The process is ALWAYS:
1. Read the raw file content
2. Parse the data (CSV parse, XML parse, JSON parse — DIFFERENT per format)
3. Validate the parsed data (same validation rules for all)
4. Save to database (same for all)

A junior dev wrote three separate classes with duplicated step 3 and 4 code. Any change to validation means editing all three classes.

**How do you share the fixed steps while allowing each subclass to handle its own parsing?**

### Your Task
1. `DataProcessor` abstract class with `final void process(String filePath)` — the template method
2. Steps `readFile()`, `validate()`, `saveToDatabase()` — provided in the base class (or can be overridden as hooks)
3. `parseData()` — abstract method that each subclass implements
4. `CsvDataProcessor`, `XmlDataProcessor`, `JsonDataProcessor`

<details>
<summary>🔍 Hint</summary>

Make `process()` final so subclasses can't change the ALGORITHM ORDER. Only make specific steps (`parseData()`) abstract so subclasses fill in their version.

```java
public final void process(String filePath) {
    String raw = readFile(filePath);   // same for all
    Object data = parseData(raw);      // DIFFERENT per subclass
    validate(data);                    // same for all
    saveToDatabase(data);              // same for all
}
```

</details>

<details>
<summary>✅ Pattern Reveal</summary>

**Pattern: Template Method**

Why: The algorithm structure (read→parse→validate→save) is fixed. Only the parsing step varies. Template Method defines the skeleton and delegates the variable step to subclasses.

</details>

### Starter Code

```java
import java.util.List;

public record ParsedRecord(String key, String value) {}

// Abstract class with template method
public abstract class DataProcessor {

    // TEMPLATE METHOD — final, defines the algorithm skeleton
    public final void process(String filePath) {
        System.out.println("\n--- Processing: " + filePath + " ---");
        String rawContent = readFile(filePath);
        List<ParsedRecord> data = parseData(rawContent);
        validate(data);
        saveToDatabase(data);
        System.out.println("Done processing " + filePath);
    }

    // Step 1: same for all — read file
    protected String readFile(String filePath) {
        System.out.println("[Read] Reading file: " + filePath);
        // Simulate file reading
        return "raw content from " + filePath;
    }

    // Step 2: ABSTRACT — subclasses provide this
    protected abstract List<ParsedRecord> parseData(String rawContent);

    // Step 3: same for all — validate
    protected void validate(List<ParsedRecord> data) {
        System.out.println("[Validate] Validating " + data.size() + " records");
        data.forEach(r -> {
            if (r.key() == null || r.value() == null) {
                throw new IllegalArgumentException("Invalid record: " + r);
            }
        });
        System.out.println("[Validate] All records valid");
    }

    // Step 4: same for all — save (can be overridden as a "hook")
    protected void saveToDatabase(List<ParsedRecord> data) {
        System.out.println("[Save] Saving " + data.size() + " records to database");
    }
}

// Concrete subclass — only overrides parseData()
public class CsvDataProcessor extends DataProcessor {
    @Override
    protected List<ParsedRecord> parseData(String rawContent) {
        System.out.println("[Parse-CSV] Parsing CSV data");
        // Simulate CSV parsing: "name,Alice\nage,30"
        return List.of(
            new ParsedRecord("name", "Alice"),
            new ParsedRecord("age", "30")
        );
    }
}

// TODO: XmlDataProcessor (XML parsing simulation)
// TODO: JsonDataProcessor (JSON parsing simulation)

// Test:
class Main {
    public static void main(String[] args) {
        new CsvDataProcessor().process("report.csv");
        // TODO: new XmlDataProcessor().process("data.xml");
        // TODO: new JsonDataProcessor().process("input.json");
    }
}
```

---

## Problem 2: Game Turn Sequence

### Scenario
In a board game, every player's TURN follows the same structure:
1. **Roll dice** (same for all)
2. **Make move** — DIFFERENT per player type (Human: asks for input, AI: calculates best move)
3. **Check win condition** (same for all)
4. **Update score** (same for all)

Two player types: `HumanPlayer` and `AIPlayer`

### Your Task
1. `GamePlayer` abstract class with `final void takeTurn()` as template method
2. `makeMove()` is abstract — each subclass decides HOW to move
3. `HumanPlayer` — simulates user choosing a position
4. `AIPlayer` — always picks a "smart" position (can be random for simulation)

### Starter Code

```java
import java.util.Random;

public abstract class GamePlayer {
    protected final String name;
    protected int score = 0;
    private final Random random = new Random();

    public GamePlayer(String name) { this.name = name; }

    // TEMPLATE METHOD
    public final void takeTurn() {
        System.out.println("\n" + name + "'s turn:");
        int diceRoll = rollDice();
        System.out.println("Rolled: " + diceRoll);
        int position = makeMove(diceRoll);
        System.out.println("Moved to position: " + position);
        if (checkWin(position)) {
            System.out.println(name + " WINS!");
        }
        updateScore(position);
    }

    protected int rollDice() {
        return new Random().nextInt(6) + 1;  // same for all
    }

    // ABSTRACT — subclasses decide how to move
    protected abstract int makeMove(int diceRoll);

    protected boolean checkWin(int position) {
        return position >= 100;  // same win condition
    }

    protected void updateScore(int position) {
        score += position;
        System.out.println(name + " score: " + score);
    }
}

public class HumanPlayer extends GamePlayer {
    private int currentPosition = 0;

    public HumanPlayer(String name) { super(name); }

    @Override
    protected int makeMove(int diceRoll) {
        // Human player moves forward by dice roll
        currentPosition += diceRoll;
        System.out.println("[Human] Moving forward by " + diceRoll);
        return currentPosition;
    }
}

public class AIPlayer extends GamePlayer {
    private int currentPosition = 0;

    public AIPlayer(String name) { super(name); }

    @Override
    protected int makeMove(int diceRoll) {
        // AI adds bonus to dice roll (always moves optimally)
        int bonus = 2;
        currentPosition += diceRoll + bonus;
        System.out.println("[AI] Moving forward by " + diceRoll + " + " + bonus + " bonus");
        return currentPosition;
    }
}

// Test:
class Main {
    public static void main(String[] args) {
        GamePlayer human = new HumanPlayer("Alice");
        GamePlayer ai    = new AIPlayer("DeepBlue");

        human.takeTurn();
        ai.takeTurn();
        human.takeTurn();
        ai.takeTurn();
    }
}
```

---

## Problem 3: Report Generator

### Scenario
Your system generates three types of reports: **Sales Report**, **Inventory Report**, **HR Report**

Every report follows:
1. Gather data (DIFFERENT per report type)
2. Format header (DIFFERENT per report: different titles)
3. Format body (DIFFERENT per report)
4. Add footer with timestamp (SAME for all)
5. Export (SAME — saves to a file)

### Starter Code

```java
import java.time.Instant;

public abstract class ReportGenerator {

    // Template method
    public final String generate() {
        StringBuilder report = new StringBuilder();
        report.append(formatHeader()).append("\n");
        report.append(formatBody()).append("\n");
        report.append(formatFooter());
        export(report.toString());
        return report.toString();
    }

    protected abstract String formatHeader();
    protected abstract String formatBody();

    // Hook: subclasses CAN override but don't have to
    protected String formatFooter() {
        return "--- Generated at: " + Instant.now() + " ---";
    }

    protected void export(String content) {
        System.out.println("Exporting report to file...");
        System.out.println(content);
    }
}

public class SalesReportGenerator extends ReportGenerator {
    @Override
    protected String formatHeader() {
        return "=== SALES REPORT ===\nPeriod: Q4 2024";
    }

    @Override
    protected String formatBody() {
        return "Total Sales: ₹12,50,000\nTop Product: Laptop\nUnits Sold: 450";
    }
}

// TODO: InventoryReportGenerator, HRReportGenerator

class Main {
    public static void main(String[] args) {
        new SalesReportGenerator().generate();
    }
}
```
