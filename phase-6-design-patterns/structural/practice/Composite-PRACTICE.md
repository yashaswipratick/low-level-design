# Composite — Practice Problems
> Goal: Treat individual objects and groups of objects UNIFORMLY.

---

## Key Intuition
**Composite = file system.** A file and a folder are both "things you can open." A folder contains files AND other folders. You can call `getSize()` on a file (returns its size) OR on a folder (returns the sum of all children's sizes). Same interface, different behavior.

The pattern has two types:
- **Leaf** — no children, does the real work
- **Composite** — holds children (leaves or other composites), delegates to them

---

## Problem 1: Organization Chart — Calculate Total Salary

### Scenario
A company has employees and managers. A Manager IS an Employee (has a salary), but also manages a TEAM of other employees.

You want: `company.getTotalSalary()` → returns the total salary bill for the entire organization.

The trick: a Manager's total cost = their own salary + all their team's salaries (recursively).

```
CEO (₹500,000)
  ├── VP Engineering (₹300,000)
  │     ├── Engineer A (₹100,000)
  │     └── Engineer B (₹100,000)
  └── VP Marketing (₹250,000)
        └── Marketer C (₹80,000)

Total = 500K + 300K + 100K + 100K + 250K + 80K = ₹1,330,000
```

**How do you calculate total salary with ONE method that works on both individual employees AND managers?**

### Your Task
1. Which pattern treats individuals and groups uniformly?
2. Create `Employee` interface with `double getSalary()` and `String getName()`
3. `IndividualEmployee` (Leaf) — just has a name and salary
4. `Manager` (Composite) — has a name, salary, and a list of subordinates

<details>
<summary>🔍 Hint</summary>

`Manager.getSalary()` = own salary + `subordinates.stream().mapToDouble(Employee::getSalary).sum()`

The recursive call works whether subordinates are `IndividualEmployee` or other `Manager` objects — that's the power of Composite.

</details>

<details>
<summary>✅ Pattern Reveal</summary>

**Pattern: Composite**

Why: You want to call `getSalary()` on both an individual and a whole department. The composite holds a list of the same interface — it doesn't care if children are leaves or other composites.

</details>

### Starter Code

```java
import java.util.ArrayList;
import java.util.List;

// Component interface
public interface Employee {
    String getName();
    double getSalary();   // for Manager: includes all subordinates recursively
    void print(String indent);
}

// Leaf
public class IndividualEmployee implements Employee {
    private final String name;
    private final double salary;

    public IndividualEmployee(String name, double salary) {
        this.name = name;
        this.salary = salary;
    }

    @Override
    public String getName() { return name; }

    @Override
    public double getSalary() { return salary; }

    @Override
    public void print(String indent) {
        System.out.println(indent + name + " (₹" + salary + ")");
    }
}

// Composite
public class Manager implements Employee {
    private final String name;
    private final double salary;
    private final List<Employee> subordinates = new ArrayList<>();

    public Manager(String name, double salary) {
        this.name = name;
        this.salary = salary;
    }

    public void add(Employee e)    { subordinates.add(e); }
    public void remove(Employee e) { subordinates.remove(e); }

    @Override
    public String getName() { return name; }

    @Override
    public double getSalary() {
        // TODO: return own salary + sum of all subordinates' salaries (recursive)
        return salary + subordinates.stream().mapToDouble(Employee::getSalary).sum();
    }

    @Override
    public void print(String indent) {
        System.out.println(indent + "[Manager] " + name + " (₹" + salary + ")");
        subordinates.forEach(e -> e.print(indent + "  "));
    }
}

// Test:
class Main {
    public static void main(String[] args) {
        Manager ceo = new Manager("CEO", 500_000);

        Manager vpEng = new Manager("VP Engineering", 300_000);
        vpEng.add(new IndividualEmployee("Engineer A", 100_000));
        vpEng.add(new IndividualEmployee("Engineer B", 100_000));

        Manager vpMkt = new Manager("VP Marketing", 250_000);
        vpMkt.add(new IndividualEmployee("Marketer C", 80_000));

        ceo.add(vpEng);
        ceo.add(vpMkt);

        ceo.print("");
        System.out.println("\nTotal salary bill: ₹" + ceo.getSalary());
        System.out.println("VP Engineering total: ₹" + vpEng.getSalary());  // ₹500,000
    }
}
```

---

## Problem 2: Menu System (Restaurant)

### Scenario
A restaurant has a menu. The menu has sections (Starters, Mains, Desserts). Each section has items. Some sections have sub-sections (e.g., "Mains" has "Veg" and "Non-Veg" sub-sections).

You want to:
1. Print the full menu tree
2. Count total items in any section (recursively)

```
MENU
├── Starters
│   ├── Soup ₹120
│   └── Salad ₹150
├── Mains
│   ├── Veg
│   │   ├── Pasta ₹280
│   │   └── Risotto ₹300
│   └── Non-Veg
│       └── Chicken ₹350
└── Desserts
    └── Ice Cream ₹80
```

### Your Task
1. `MenuComponent` interface: `String getName()`, `double getPrice()`, `int countItems()`, `void print(String indent)`
2. `MenuItem` (Leaf) — has name and price
3. `MenuSection` (Composite) — has name, list of `MenuComponent` children

### Starter Code

```java
import java.util.ArrayList;
import java.util.List;

public interface MenuComponent {
    String getName();
    double getPrice();   // MenuItem: its price; MenuSection: 0 (or total)
    int countItems();    // MenuItem: 1; MenuSection: sum of all children
    void print(String indent);
}

public class MenuItem implements MenuComponent {
    private final String name;
    private final double price;

    public MenuItem(String name, double price) {
        this.name = name;
        this.price = price;
    }

    @Override public String getName()   { return name; }
    @Override public double getPrice()  { return price; }
    @Override public int countItems()   { return 1; }
    @Override public void print(String indent) {
        System.out.printf("%s%s  ₹%.0f%n", indent, name, price);
    }
}

public class MenuSection implements MenuComponent {
    private final String name;
    private final List<MenuComponent> children = new ArrayList<>();

    public MenuSection(String name) { this.name = name; }

    public void add(MenuComponent component) { children.add(component); }

    @Override public String getName()   { return name; }
    @Override public double getPrice()  { return 0; }

    @Override
    public int countItems() {
        // TODO: sum of all children's countItems()
        return children.stream().mapToInt(MenuComponent::countItems).sum();
    }

    @Override
    public void print(String indent) {
        System.out.println(indent + "[" + name + "]");
        children.forEach(c -> c.print(indent + "  "));
    }
}

// Test: build the menu above and call print("") and countItems()
```

---

## Problem 3: Math Expression Tree

### Scenario
You want to evaluate math expressions as a tree:
```
      +
    /   \
   *     5
  / \
 3   4
```
This represents `(3 * 4) + 5 = 17`

Each node is either a **Number** (leaf) or an **Operation** (composite with two children).

### Your Task
1. `Expression` interface: `int evaluate()`
2. `Number` (Leaf) — holds an integer value
3. `Addition`, `Multiplication`, `Subtraction` (Composites) — each holds two `Expression` children

### Starter Code

```java
public interface Expression {
    int evaluate();
    String toFormula();  // human-readable representation
}

public class Number implements Expression {
    private final int value;
    public Number(int value) { this.value = value; }

    @Override public int evaluate()       { return value; }
    @Override public String toFormula()   { return String.valueOf(value); }
}

public class Addition implements Expression {
    private final Expression left;
    private final Expression right;

    public Addition(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override public int evaluate()       { return left.evaluate() + right.evaluate(); }
    @Override public String toFormula()   { return "(" + left.toFormula() + " + " + right.toFormula() + ")"; }
}

// TODO: Multiplication and Subtraction

// Test:
class Main {
    public static void main(String[] args) {
        // (3 * 4) + 5
        Expression expr = new Addition(
            new Multiplication(new Number(3), new Number(4)),
            new Number(5)
        );

        System.out.println(expr.toFormula());   // ((3 * 4) + 5)
        System.out.println(expr.evaluate());    // 17

        // (10 - 3) * (2 + 4)
        Expression expr2 = new Multiplication(
            new Subtraction(new Number(10), new Number(3)),
            new Addition(new Number(2), new Number(4))
        );
        System.out.println(expr2.toFormula());  // ((10 - 3) * (2 + 4))
        System.out.println(expr2.evaluate());   // 42
    }
}
```
