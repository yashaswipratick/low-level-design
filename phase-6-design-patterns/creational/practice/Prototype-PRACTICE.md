# Prototype — Practice Problems
> Goal: Recognize when cloning an existing object is faster/simpler than building from scratch.

---

## Problem 1: Document Template Cloning

### Scenario
You work at a legal firm. Every new contract starts with a **template document** that has:
- Standard headers and footers (pre-filled)
- A list of standard clauses (20+ items, each pre-configured)
- Formatting settings (fonts, margins, page size)

Creating a contract from scratch takes 50ms (parsing templates, deep copying clauses).

For each new client, you need a fresh copy of the template that you can then customize (change client name, modify specific clauses).

**Should you create a new object from scratch each time, or copy an existing template?**

### Your Task
1. Which pattern solves this?
2. Implement `DocumentTemplate` with a `clone()` method
3. The clone must be a **deep copy** — changing a clause in the clone must NOT affect the original

<details>
<summary>🔍 Hint 1</summary>
"Clone an existing object" = Prototype pattern. The key is: you already have a configured object, and you want more copies of it to customize independently.
</details>

<details>
<summary>🔍 Hint 2</summary>
**Shallow copy** copies the list reference — both the original and clone point to the SAME list. **Deep copy** creates a new list — changes in one don't affect the other.
</details>

<details>
<summary>✅ Pattern Reveal</summary>

**Pattern: Prototype**

Why: Creating the template is expensive (50ms). Once you have the template, cloning it is cheap. Each clone is independent — modifying one doesn't affect others.

</details>

### Starter Code

```java
import java.util.ArrayList;
import java.util.List;

public class Clause {
    private String text;

    public Clause(String text) { this.text = text; }

    // Important: deep copy of Clause
    public Clause copy() { return new Clause(this.text); }

    public void setText(String text) { this.text = text; }
    public String getText() { return text; }
}

public class DocumentTemplate {
    private String title;
    private String header;
    private String footer;
    private List<Clause> clauses;

    public DocumentTemplate(String title, String header, String footer) {
        this.title = title;
        this.header = header;
        this.footer = footer;
        this.clauses = new ArrayList<>();
    }

    public void addClause(Clause clause) { clauses.add(clause); }

    // TODO: implement deep copy clone
    public DocumentTemplate clone() {
        DocumentTemplate copy = new DocumentTemplate(this.title, this.header, this.footer);
        // TODO: deep copy the clauses list
        // copy.clauses = ... (each clause must be a new Clause object)
        return copy;
    }

    public void setTitle(String title) { this.title = title; }
    public String getTitle() { return title; }
    public List<Clause> getClauses() { return clauses; }

    @Override
    public String toString() {
        return "Title: " + title + " | Clauses: " + clauses.stream().map(Clause::getText).toList();
    }
}

// Test:
class Main {
    public static void main(String[] args) {
        // Create the master template ONCE (expensive)
        DocumentTemplate masterTemplate = new DocumentTemplate("Contract", "ACME Corp", "Page {n}");
        masterTemplate.addClause(new Clause("Party agrees to terms..."));
        masterTemplate.addClause(new Clause("Liability is limited to..."));
        System.out.println("Master: " + masterTemplate);

        // Clone for each client (cheap)
        DocumentTemplate aliceContract = masterTemplate.clone();
        aliceContract.setTitle("Alice Smith Contract");
        aliceContract.getClauses().get(0).setText("Alice agrees to terms...");  // modify clone

        DocumentTemplate bobContract = masterTemplate.clone();
        bobContract.setTitle("Bob Jones Contract");

        System.out.println("Alice:  " + aliceContract);
        System.out.println("Bob:    " + bobContract);

        // CRITICAL: master template must be unchanged
        System.out.println("Master: " + masterTemplate);
        // Master clause 1 must still say "Party agrees to terms..." not "Alice agrees..."
    }
}
```

---

## Problem 2: Game Character Clone

### Scenario
In your RPG game, a player creates a Warrior named "Sir Lancelot" with stats they spent 30 minutes configuring:
- Level 50, Health 500, Attack 120
- Equipped items: "Excalibur Sword", "Dragon Shield", "Magic Ring"
- Skills: ["Slash", "Block", "Counter"]

A friend joins the game and wants to start with the **same configuration** as a base (then rename and tweak).

**How do you give the friend a copy that starts identical but is completely independent?**

### Your Task
1. Implement `GameCharacter` with a `clone()` method
2. **Shallow copy problem:** If you copy the skills list reference, adding a skill to the clone also adds it to the original. Fix this with a deep copy.
3. Demonstrate: clone, rename, add a skill — original must be unchanged

<details>
<summary>✅ Pattern Reveal</summary>

**Pattern: Prototype**

Why: The existing configured character IS the prototype. Cloning it gives a starting point — you don't rebuild from scratch (no "create character → set level → add each item one by one").

</details>

### Starter Code

```java
import java.util.ArrayList;
import java.util.List;

public class GameCharacter {
    private String name;
    private int level;
    private int health;
    private int attack;
    private List<String> skills;
    private List<String> equipment;

    public GameCharacter(String name, int level, int health, int attack) {
        this.name = name;
        this.level = level;
        this.health = health;
        this.attack = attack;
        this.skills = new ArrayList<>();
        this.equipment = new ArrayList<>();
    }

    public void addSkill(String skill)     { skills.add(skill); }
    public void addEquipment(String item)  { equipment.add(item); }
    public void setName(String name)       { this.name = name; }

    // TODO: implement deep copy clone
    public GameCharacter clone() {
        GameCharacter copy = new GameCharacter(this.name, this.level, this.health, this.attack);
        // TODO: deep copy skills and equipment lists
        return copy;
    }

    @Override
    public String toString() {
        return name + " | Lv" + level + " | HP:" + health + " | ATK:" + attack
               + " | Skills:" + skills + " | Equip:" + equipment;
    }
}

// Test: original and clone must be fully independent
```

---

## Problem 3: Product Listing Clone

### Scenario
An e-commerce seller wants to list 50 similar products (T-shirts in different colors). Each shirt has the same:
- Description, material, care instructions
- Shipping info, return policy
- 15 attribute tags

Instead of filling out 50 forms, the seller fills ONE, then clones it 49 times and just changes the color and image URL for each.

**Implement `ProductListing` with a `copyWith(String color, String imageUrl)` method that clones and customizes in one step.**

### Starter Code

```java
import java.util.ArrayList;
import java.util.List;

public class ProductListing {
    private String name;
    private String description;
    private String material;
    private double price;
    private String color;
    private String imageUrl;
    private List<String> tags;

    public ProductListing(String name, String description, String material, double price) {
        this.name = name;
        this.description = description;
        this.material = material;
        this.price = price;
        this.tags = new ArrayList<>();
    }

    public void addTag(String tag) { tags.add(tag); }

    // Clone + customize in one step
    public ProductListing copyWith(String color, String imageUrl) {
        ProductListing copy = new ProductListing(this.name, this.description, this.material, this.price);
        copy.color    = color;
        copy.imageUrl = imageUrl;
        // TODO: deep copy tags
        copy.tags = new ArrayList<>(this.tags);
        return copy;
    }

    @Override
    public String toString() {
        return name + " | Color: " + color + " | $" + price + " | Tags: " + tags;
    }

    // setters
    public void setColor(String color)     { this.color = color; }
    public void setImageUrl(String url)    { this.imageUrl = url; }
}

// Test:
class Main {
    public static void main(String[] args) {
        ProductListing baseShirt = new ProductListing("Classic T-Shirt", "100% cotton tee", "Cotton", 19.99);
        baseShirt.addTag("cotton");
        baseShirt.addTag("casual");
        baseShirt.addTag("unisex");

        ProductListing redShirt   = baseShirt.copyWith("Red",   "img/red-tshirt.jpg");
        ProductListing blueShirt  = baseShirt.copyWith("Blue",  "img/blue-tshirt.jpg");
        ProductListing greenShirt = baseShirt.copyWith("Green", "img/green-tshirt.jpg");

        System.out.println(redShirt);
        System.out.println(blueShirt);
        System.out.println(greenShirt);

        // Adding a tag to redShirt must NOT appear in blueShirt
        redShirt.addTag("sale");
        System.out.println("Red after tag: " + redShirt);
        System.out.println("Blue unchanged: " + blueShirt);
    }
}
```
